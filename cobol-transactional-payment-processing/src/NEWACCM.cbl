       IDENTIFICATION DIVISION.                                         
       PROGRAM-ID. NEWACCM.                                             
      ******************************************************************
      * NEW ACCOUNT CONTROLLER                                          
      ******************************************************************
       ENVIRONMENT DIVISION.                                            
       DATA DIVISION.                                                   
       WORKING-STORAGE SECTION.                                         
           EXEC SQL                                                     
            INCLUDE SQLCA                                               
           END-EXEC.                                                    
       COPY DFHAID.                                                     
       COPY NEWACCMP.                                                   
      * THE TRANSID IS BANK NEW ACCOUNT (BNA)                           
       01 WS-TRANSID PIC X(4) VALUE "BNA".                              
       01 WS-MSG PIC X(40) VALUE SPACES.                                
       01 WS-USER-INPUT.                                                
         05 WS-INPUT-EMAIL PIC X(50).                                   
         05 WS-INPUT-PASS  PIC X(50).                                   
       01 WS-STRACC-COMMAREA.                                           
         05 WS-USER-NAME PIC X(50) VALUE SPACES.                        
         04 WS-FIRST-TIME PIC X VALUE 'Y'.                              
       01 WS-COMMAREA.                                                  
         05 WS-CONTINUE PIC X.                                          
       01 WS-END PIC X(3) VALUE "END".                                  
       01 WS-SQL-CODE PIC -9(6).                                        
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
              MAPSET('NAMP')                                            
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
              MAPSET('NAMP')                                            
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
      * CREATING A NEW ACCOUNT                                          
           EXEC SQL                                                     
             INSERT INTO USERS                                          
             (EMAIL,PASSWORD)                                           
             VALUES                                                     
             (:WS-INPUT-EMAIL,:WS-INPUT-PASS)                           
           END-EXEC                                                     
      *  CHECK IF EMAIL ALREADY IN USE                                  
           IF SQLCODE = -803  THEN                                      
             MOVE 'EMAIL ALREADY IN USE' TO WS-MSG                      
             PERFORM SEND-SCREEN                                        
           END-IF                                                       
                                                                        
      * CHECK OTHER TYPES OF ERROR                                      
           IF SQLCODE  < 0 THEN                                         
             MOVE SQLCODE TO WS-SQL-CODE                                
             STRING 'ERROR= ' DELIMITED BY SIZE                         
                 WS-SQL-CODE DELIMITED BY SIZE                          
                 INTO WS-MSG                                            
             END-STRING                                                 
             PERFORM SEND-SCREEN                                        
           END-IF                                                       
      * TEMPORAL TEST TO SEE IF IT WORKS                                
           EXEC SQL                                                     
             COMMIT                                                     
           END-EXEC                                                     
      * MOVE EMAIL TO COMMAREA FOR START ACCOUNT VIEW                   
           MOVE WS-INPUT-EMAIL TO WS-USER-NAME                          
      * ROUTE TO START ACCOUNT MAP                                      
           EXEC CICS XCTL                                               
             PROGRAM('STRACCM')                                         
             COMMAREA(WS-STRACC-COMMAREA)                               
             LENGTH(51)                                                 
           END-EXEC.                                                    
       SEND-SCREEN.                                                     
           MOVE WS-MSG TO MSGO                                          
                                                                        
           EXEC CICS SEND                                               
              MAPSET('NAMP')                                            
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