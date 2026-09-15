       IDENTIFICATION DIVISION.                                         
       PROGRAM-ID. ARAT02.                                              
      ***************************************************************** 
      * ARAT02: CREATE AIRCRAFT                                         
      *                                                                 
      ***************************************************************** 
       ENVIRONMENT DIVISION.                                            
       DATA DIVISION.                                                   
       WORKING-STORAGE SECTION.                                         
           EXEC SQL                                                     
              INCLUDE SQLCA                                             
           END-EXEC.                                                    
       COPY DFHAID.                                                     
       COPY MPAT02.                                                     
                                                                        
       01 WS-COMMAREA.                                                  
          COPY ARCOMMA.                                                 
       01 WS-CICS.                                                      
          05 WS-CICS-TRANSID PIC X(4) VALUE "AT02".                     
          05 WS-CICS-MAPSET PIC X(10) VALUE "MPAT02".                   
          05 WS-CICS-MAP PIC X(10) VALUE "SCRN1".                       
          05 WS-CICS-CALEN PIC 9(2) VALUE 1.                            
       01 WS-OUT.                                                       
          05 WS-MSG PIC X(40).                                          
          05 WS-SQL-CODE PIC -9(6).                                     
       01 WS-AIRCRAFT.                                                  
          05 WS-ID PIC S9(9) COMP.                                      
          05 WS-MODEL PIC X(50).                                        
          05 WS-CAPACITY PIC S9(9) COMP.                                
          05 WS-STATUS PIC X(1).                                        
       01 WS-SEATS.                                                     
          05 WS-SEAT-CAPACITY PIC 9(3).                                 
          05 WS-SEAT-COUNTER PIC 9(3) VALUE 1.                          
          05 WS-SEAT-NUMBER  PIC X(3).                                  
       LINKAGE SECTION.                                                 
       01 DFHCOMMAREA.                                                  
          COPY ARCOMMA.                                                 
       PROCEDURE DIVISION.                                              
       MAIN-PARA.                                                       
           MOVE DFHCOMMAREA TO WS-COMMAREA                              
           IF EIBCALEN = 0 THEN                                         
              PERFORM INIT-PARA                                         
              PERFORM FIRST-TIME-PARA                                   
           ELSE                                                         
              PERFORM RECEIVE-PARA                                      
           END-IF.                                                      
       INIT-PARA.                                                       
           MOVE LOW-VALUES TO SCRN1I.                                   
       FIRST-TIME-PARA.                                                 
           MOVE 'Y' TO WS-CONTINUE OF WS-COMMAREA                       
           EXEC CICS SEND                                               
              MAPSET(WS-CICS-MAPSET)                                    
              MAP('SCRN1')                                              
              FREEKB                                                    
              ERASE                                                     
           END-EXEC                                                     
           EXEC CICS RETURN                                             
              TRANSID(WS-CICS-TRANSID)                                  
              COMMAREA(WS-COMMAREA)                                     
              LENGTH(1)                                                 
           END-EXEC.                                                    
       RECEIVE-PARA.                                                    
           EXEC CICS RECEIVE                                            
              MAPSET(WS-CICS-MAPSET)                                    
              MAP('SCRN1')                                              
              INTO(SCRN1I)                                              
           END-EXEC                                                     
           PERFORM CHECK-KEY-PARA.                                      
       CHECK-KEY-PARA.                                                  
           EVALUATE TRUE                                                
              WHEN EIBAID = DFHENTER                                    
                 PERFORM PROCESS-PARA                                   
              WHEN EIBAID = DFHPF3                                      
                 PERFORM REDIRECT-MENU-PARA                             
              WHEN OTHER                                                
                 PERFORM INVALID-KEY-PARA                               
           END-EVALUATE.                                                
       INVALID-KEY-PARA.                                                
           MOVE 'INVALID KEY' TO WS-MSG                                 
           PERFORM SEND-SCREEN-PARA.                                    
       PROCESS-PARA.                                                    
           IF AT02MDI = SPACES                                          
           OR AT02CAI = SPACES                                          
           OR AT02STI = SPACES                                          
           THEN                                                         
              MOVE 'YOU MUST PROVIDE ALL FIELDS' TO WS-MSG              
              PERFORM SEND-SCREEN-PARA                                  
           END-IF                                                       
                                                                        
           IF AT02CAI NOT NUMERIC THEN                                  
              MOVE 'CAPACITY MUST BE NUMERIC' TO WS-MSG                 
              PERFORM SEND-SCREEN-PARA                                  
           END-IF                                                       
                                                                        
           MOVE AT02MDI TO WS-MODEL                                     
           MOVE AT02CAI TO WS-CAPACITY                                  
           MOVE AT02CAI TO WS-SEAT-CAPACITY                             
           MOVE AT02STI TO WS-STATUS                                    
                                                                        
           EXEC SQL                                                     
              INSERT INTO AIRCRAFT                                      
              (MODEL, CAPACITY, STATUS)                                 
              VALUES                                                    
              (:WS-MODEL,:WS-CAPACITY,:WS-STATUS)                       
           END-EXEC                                                     
                                                                        
           IF SQLCODE < 0 THEN                                          
              MOVE SQLCODE TO WS-SQL-CODE                               
                                                                        
              STRING 'SOMETHING WENT WRONG: ' DELIMITED BY SIZE         
                     WS-SQL-CODE DELIMITED BY SIZE                      
                INTO WS-MSG                                             
              END-STRING                                                
                                                                        
              PERFORM SEND-SCREEN-PARA                                  
           END-IF                                                       
                                                                        
           IF SQLCODE = 0 THEN                                          
              EXEC SQL                                                  
                 SET :WS-ID = IDENTITY_VAL_LOCAL()                      
              END-EXEC                                                  
           END-IF                                                       
                                                                        
           PERFORM VARYING WS-SEAT-COUNTER FROM 1 BY 1                  
           UNTIL WS-SEAT-COUNTER > WS-SEAT-CAPACITY                     
              MOVE WS-SEAT-COUNTER TO WS-SEAT-NUMBER                    
              PERFORM INSERT-SEAT-PARA                                  
           END-PERFORM                                                  
                                                                        
           MOVE 'AIRCRAFT CREATED' TO WS-MSG                            
           PERFORM SEND-SCREEN-PARA.                                    
       INSERT-SEAT-PARA.                                                
                                                                        
           EXEC SQL                                                     
              INSERT INTO SEAT                                          
              (AIRCRAFT_ID, SEAT_NUMBER)                                
              VALUES (:WS-ID, :WS-SEAT-NUMBER)                          
           END-EXEC                                                     
                                                                        
           IF SQLCODE < 0 THEN                                          
              MOVE 'SOMETHING WENT WRONG. TRY AGAIN' TO WS-MSG          
                                                                        
              EXEC SQL                                                  
                 ROLLBACK                                               
              END-EXEC                                                  
                                                                        
              PERFORM SEND-SCREEN-PARA                                  
           END-IF.                                                      
       SEND-SCREEN-PARA.                                                
           MOVE WS-MSG TO APMSGO                                        
                                                                        
           EXEC CICS SEND                                               
              MAPSET(WS-CICS-MAPSET)                                    
              MAP('SCRN1')                                              
              FROM(SCRN1O)                                              
              ERASE                                                     
              FREEKB                                                    
           END-EXEC                                                     
           EXEC CICS RETURN                                             
              TRANSID(WS-CICS-TRANSID)                                  
              COMMAREA(WS-COMMAREA)                                     
              LENGTH(1)                                                 
           END-EXEC.                                                    
                                                                        
       REDIRECT-MENU-PARA.                                              
           EXEC CICS XCTL                                               
              PROGRAM('ARAT00')                                         
           END-EXEC.                                                    
       EXIT-PARA.                                                       
           EXEC CICS                                                    
              SEND CONTROL                                              
              ERASE                                                     
              FREEKB                                                    
           END-EXEC                                                     
                                                                        
           EXEC CICS RETURN                                             
           END-EXEC.                                                    