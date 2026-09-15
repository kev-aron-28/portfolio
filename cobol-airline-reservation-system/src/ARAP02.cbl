       IDENTIFICATION DIVISION.                                         
       PROGRAM-ID. ARAP02.                                              
      ***************************************************************** 
      * ARAP02: CREATE AIRPORT                                          
      *                                                                 
      ***************************************************************** 
       ENVIRONMENT DIVISION.                                            
       DATA DIVISION.                                                   
       WORKING-STORAGE SECTION.                                         
           EXEC SQL                                                     
              INCLUDE SQLCA                                             
           END-EXEC.                                                    
       COPY DFHAID.                                                     
       COPY MPAP02.                                                     
                                                                        
       01 WS-COMMAREA.                                                  
          COPY ARCOMMA.                                                 
       01 WS-CICS.                                                      
          05 WS-CICS-TRANSID PIC X(4) VALUE "AP02".                     
          05 WS-CICS-MAPSET PIC X(10) VALUE "MPAP02".                   
          05 WS-CICS-MAP PIC X(10) VALUE "SCRN1".                       
          05 WS-CICS-CALEN PIC 9(2) VALUE 1.                            
       01 WS-OUT.                                                       
          05 WS-MSG PIC X(40).                                          
          05 WS-SQL-CODE PIC -9(6).                                     
       01 WS-AIRPORT.                                                   
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
           IF (AP02CDI = LOW-VALUES OR AP02CDI = SPACES)                
           OR (AP02NAI = LOW-VALUES OR AP02NAI = SPACES)                
           OR (AP02CYI = LOW-VALUES OR AP02CYI = SPACES)                
           OR (AP02CRI = LOW-VALUES OR AP02CRI = SPACES)                
           THEN                                                         
              MOVE 'YOU MUST PROVIDE ALL FIELDS' TO WS-MSG              
              PERFORM SEND-SCREEN-PARA                                  
           END-IF                                                       
                                                                        
           MOVE AP02CDI TO WS-CODE                                      
           MOVE AP02NAI TO WS-NAME                                      
           MOVE AP02CYI TO WS-CITY                                      
           MOVE AP02CRI TO WS-COUNTRY                                   
                                                                        
           EXEC SQL                                                     
              INSERT INTO AIRPORT                                       
              (CODE, NAME, CITY, COUNTRY)                               
              VALUES                                                    
              (:WS-CODE,:WS-NAME,:WS-CITY,:WS-COUNTRY)                  
           END-EXEC                                                     
                                                                        
           IF SQLCODE = -803 THEN                                       
              MOVE 'CODE ALREADY IN USE' TO WS-MSG                      
           END-IF                                                       
                                                                        
           IF SQLCODE < 0 THEN                                          
              MOVE SQLCODE TO WS-SQL-CODE                               
              STRING 'SOMETHING WENT WRONG: ' DELIMITED BY SIZE         
                     WS-SQL-CODE DELIMITED BY SIZE                      
                INTO WS-MSG                                             
              END-STRING                                                
           END-IF                                                       
                                                                        
           IF SQLCODE = 0 THEN                                          
              MOVE 'AIRPORT CREATED' TO WS-MSG                          
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