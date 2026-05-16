       IDENTIFICATION DIVISION.                                         
       PROGRAM-ID. LOGINM.                                              
      ******************************************************************
      * LOGIN PAGE CONTROLLER                                           
      ******************************************************************
       ENVIRONMENT DIVISION.                                            
       DATA DIVISION.                                                   
       WORKING-STORAGE SECTION.                                         
           EXEC SQL                                                     
            INCLUDE SQLCA                                               
           END-EXEC.                                                    
       COPY DFHAID.                                                     
       COPY LOGINMP.                                                    
      * THE TRANSID IS BANK LOGIN (BLG)                                 
       01 WS-TRANSID PIC X(4) VALUE "BLG".                              
       01 WS-MSG PIC X(40) VALUE SPACES.                                
       01 WS-USER-INPUT.                                                
         05 WS-INPUT-EMAIL PIC X(50).                                   
         05 WS-INPUT-PASS  PIC X(50).                                   
       01 WS-DB-EMAIL PIC X(50).                                        
       01 WS-DB-PASS  PIC X(50).                                        
       01 WS-COMMAREA.                                                  
         05 WS-CONTINUE PIC X.                                          
       01 WS-END PIC X(3) VALUE "END".                                  
       LINKAGE SECTION.                                                 
       01 DFHCOMMAREA.                                                  
         05 LS-CONTINUE PIC X.                                          
                                                                        
       PROCEDURE DIVISION.                                              
       MAIN.                                                            
      * CHECK IF ITS THE FIRST TIME                                     
           IF EIBCALEN = ZERO THEN                                      
              PERFORM INIT-PROGRAM                                      
              PERFORM FIRST-TIME                                        
           ELSE                                                         
              MOVE DFHCOMMAREA TO WS-COMMAREA                           
              PERFORM RECEIVE-SCREEN                                    
           END-IF.                                                      
       INIT-PROGRAM.                                                    
           MOVE LOW-VALUES TO SCRN1I.                                   
       FIRST-TIME.                                                      
           EXEC CICS SEND                                               
              MAPSET('LOGINMP')                                         
              MAP('SCRN1')                                              
              FREEKB                                                    
              ERASE                                                     
           END-EXEC                                                     
                                                                        
           EXEC CICS RETURN                                             
              TRANSID(WS-TRANSID)                                       
              COMMAREA(WS-COMMAREA)                                     
              LENGTH(1)                                                 
           END-EXEC.                                                    
       RECEIVE-SCREEN.                                                  
           EXEC CICS RECEIVE                                            
              MAPSET('LOGINMP')                                         
              MAP('SCRN1')                                              
              INTO(SCRN1I)                                              
           END-EXEC                                                     
           PERFORM CHECK-KEY.                                           
       CHECK-KEY.                                                       
           EVALUATE TRUE                                                
              WHEN EIBAID = DFHENTER                                    
                PERFORM EVALUATE-DATA                                   
              WHEN EIBAID = DFHPF3                                      
                PERFORM EXIT-PROGRAM                                    
                EXIT                                                    
             WHEN OTHER                                                 
                MOVE 'INVALID KEY' TO WS-MSG                            
                MOVE WS-MSG TO MSGO                                     
                PERFORM SEND-SCREEN                                     
           END-EVALUATE.                                                
       EVALUATE-DATA.                                                   
           IF EMAILL = 0 THEN                                           
              MOVE 'EMAIL IS REQURIED' TO WS-MSG                        
              PERFORM SEND-SCREEN                                       
              EXIT                                                      
           END-IF                                                       
           IF PASSL = 0 THEN                                            
              MOVE 'PASSWORD IS REQURIED' TO WS-MSG                     
              PERFORM SEND-SCREEN                                       
              EXIT                                                      
           END-IF                                                       
           MOVE EMAILI TO WS-INPUT-EMAIL                                
           MOVE PASSI TO WS-INPUT-PASS                                  
      * HERE SHOULD BE AUTH                                             
           EXEC SQL                                                     
              SELECT EMAIL,PASSWORD                                     
              INTO :WS-DB-EMAIL,:WS-DB-PASS                             
              FROM USERS                                                
              WHERE EMAIL=:WS-INPUT-EMAIL                               
           END-EXEC                                                     
                                                                        
           IF SQLCODE = 100  THEN                                       
             MOVE 'NO ACCOUNT FOUND' TO WS-MSG                          
           ELSE                                                         
             MOVE 'FAIL TO LOGIN' TO WS-MSG                             
           END-IF                                                       
           PERFORM SEND-SCREEN.                                         
       SEND-SCREEN.                                                     
           MOVE WS-MSG TO MSGO                                          
                                                                        
           EXEC CICS SEND                                               
              MAPSET('LOGINMP')                                         
              MAP('SCRN1')                                              
              FROM(SCRN1O)                                              
              ERASE                                                     
              FREEKB                                                    
           END-EXEC                                                    
                                                                       
           EXEC CICS RETURN                                            
              TRANSID(WS-TRANSID)                                      
              COMMAREA(WS-COMMAREA)                                    
              LENGTH(1)                                                
           END-EXEC.                                                   
       EXIT-PROGRAM.                                                   
           EXEC CICS                                                   
             SEND CONTROL                                              
             ERASE                                                     
             FREEKB                                                    
           END-EXEC                                                    
           EXEC CICS RETURN                                            
           END-EXEC.                                                   