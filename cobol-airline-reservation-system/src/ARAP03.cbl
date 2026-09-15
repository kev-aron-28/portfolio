       IDENTIFICATION DIVISION.                                         
       PROGRAM-ID. ARAP03.                                              
      ***************************************************************** 
      * ARAP3: AIRPORT UPDATE                                           
      *                                                                 
      ***************************************************************** 
       ENVIRONMENT DIVISION.                                            
       DATA DIVISION.                                                   
       WORKING-STORAGE SECTION.                                         
           EXEC SQL                                                     
              INCLUDE SQLCA                                             
           END-EXEC.                                                    
       COPY DFHAID.                                                     
       COPY MPAP03.                                                     
                                                                        
       01 WS-COMMAREA.                                                  
          COPY ARCOMMA.                                                 
       01 WS-CICS.                                                      
          05 WS-CICS-TRANSID PIC X(4) VALUE "AP03".                     
          05 WS-CICS-MAPSET PIC X(10) VALUE "MPAP03".                   
          05 WS-CICS-MAP PIC X(10) VALUE "SCRN1".                       
          05 WS-CICS-CALEN PIC 9(2) VALUE 1.                            
       01 WS-OUT.                                                       
          05 WS-MSG PIC X(40).                                          
          05 WS-SQL-CODE PIC -9(6).                                     
       01 WS-ID PIC S9(9) COMP.                                         
       01 WS-AIRPORT-OLD.                                               
          05 WS-CODE-OLD PIC X(3).                                      
          05 WS-NAME-OLD PIC X(100).                                    
          05 WS-CITY-OLD PIC X(50).                                     
          05 WS-COUNTRY-OLD PIC X(50).                                  
       01 WS-AIRPORT-UPDATE.                                            
          05 WS-CODE PIC X(3).                                          
          05 WS-NAME PIC X(100).                                        
          05 WS-CITY PIC X(50).                                         
          05 WS-COUNTRY PIC X(50).                                      
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
           IF AP03IDI = LOW-VALUES OR AP03IDI = SPACES THEN             
              MOVE 'YOU MUST PROVIDE THE ID' TO WS-MSG                  
              PERFORM SEND-SCREEN-PARA                                  
           END-IF                                                       
                                                                        
           IF AP03IDI NOT NUMERIC THEN                                  
              MOVE 'ID MUST BE NUMERIC' TO WS-MSG                       
              PERFORM SEND-SCREEN-PARA                                  
           END-IF                                                       
                                                                        
           MOVE AP03IDI TO WS-ID                                        
                                                                        
           EXEC SQL                                                     
              SELECT CODE, NAME, CITY, COUNTRY                          
              INTO                                                      
              :WS-CODE-OLD,                                             
              :WS-NAME-OLD,                                             
              :WS-CITY-OLD,                                             
              :WS-COUNTRY-OLD                                           
              FROM AIRPORT                                              
              WHERE AIRPORT_ID = :WS-ID                                 
           END-EXEC                                                     
                                                                        
           IF SQLCODE = 100 THEN                                        
              MOVE 'NO AIRPORT FOUND' TO WS-MSG                         
              PERFORM SEND-SCREEN-PARA                                  
           END-IF                                                       
                                                                        
           IF SQLCODE < 0 THEN                                          
              MOVE SQLCODE TO WS-SQL-CODE                               
              STRING 'SOMETHING WENT WRONG: ' DELIMITED BY SIZE         
                     WS-SQL-CODE DELIMITED BY SIZE                      
                INTO WS-MSG                                             
              END-STRING                                                
              PERFORM SEND-SCREEN-PARA                                  
           END-IF                                                       
                                                                        
           IF                                                           
           (AP03CDI = LOW-VALUES OR AP03CDI = SPACES)                   
           AND (AP03NAI = LOW-VALUES OR AP03NAI = SPACES)               
           AND (AP03CYI = LOW-VALUES OR AP03CYI = SPACES)               
           AND (AP03CRI = LOW-VALUES OR AP03CRI = SPACES)               
           THEN                                                         
              MOVE 'YOU MUST UPDATE AT LEAST ONE PROPERTY' TO           
              WS-MSG                                                    
              PERFORM SEND-SCREEN-PARA                                  
           END-IF                                                       
                                                                        
           IF AP03CDL = 0 THEN                                          
              MOVE WS-CODE-OLD TO WS-CODE                               
           ELSE                                                         
              MOVE AP03CDI TO WS-CODE                                   
           END-IF                                                       
                                                                        
           IF AP03NAL = 0 THEN                                          
              MOVE WS-NAME-OLD TO WS-NAME                               
           ELSE                                                         
              MOVE AP03NAI TO WS-NAME                                   
           END-IF                                                       
                                                                        
           IF AP03CYL = 0 THEN                                          
              MOVE WS-CITY-OLD TO WS-CITY                               
           ELSE                                                         
              MOVE AP03CYI TO WS-CITY                                   
           END-IF                                                       
                                                                        
           IF AP03CRL = 0 THEN                                          
              MOVE WS-COUNTRY-OLD TO WS-COUNTRY                         
           ELSE                                                         
              MOVE AP03CRI TO WS-COUNTRY                                
           END-IF                                                       
                                                                        
           EXEC SQL                                                     
              UPDATE AIRPORT                                            
              SET CODE = :WS-CODE,                                      
                  NAME = :WS-NAME,                                      
                  CITY = :WS-CITY,                                      
                  COUNTRY = :WS-COUNTRY                                 
              WHERE AIRPORT_ID = :WS-ID                                 
           END-EXEC                                                     
                                                                        
           IF SQLCODE = 100 THEN                                        
              MOVE 'NO AIRPORT FOUND' TO WS-MSG                         
           END-IF                                                       
                                                                        
           IF SQLCODE < 0 THEN                                          
              MOVE 'SOMETHING WENT WRONG' TO WS-MSG                     
           END-IF                                                       
                                                                        
           IF SQLCODE = 0 THEN                                          
              MOVE 'AIRPORT UPDATED' TO WS-MSG                          
           END-IF                                                       
                                                                        
           PERFORM SEND-SCREEN-PARA.                                    
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
              PROGRAM('ARAP00')                                         
           END-EXEC.                                                    
       EXIT-PARA.                                                       
           EXEC CICS                                                    
              SEND CONTROL                                              
              ERASE                                                     
              FREEKB                                                    
           END-EXEC                                                     
                                                                        
           EXEC CICS RETURN                                             
           END-EXEC.                                                                     
