       IDENTIFICATION DIVISION.                                         
       PROGRAM-ID. WITHDRAM.                                            
      ******************************************************************
      * TRANSFER CONTROLLER VIEW                                        
      ******************************************************************
       ENVIRONMENT DIVISION.                                            
       DATA DIVISION.                                                   
       WORKING-STORAGE SECTION.                                         
           EXEC SQL                                                     
            INCLUDE SQLCA                                               
           END-EXEC.                                                    
       COPY DFHAID.                                                     
       COPY TFMP.                                                       
      * THE TRANSID IS BANK WITHDRAW ACCOUNT                            
       01 WS-TRANSID PIC X(4) VALUE "BTA".                              
       01 WS-MSG PIC X(40) VALUE SPACES.                                
       01 WS-USER-INPUT.                                                
         05 WS-OPT PIC 9(2).                                            
       01 WS-USER-ID-DISPLAY PIC ZZZZZZZZ9.                             
       01 DB-USER-BALANCE PIC S9(15)V9(2) COMP-3.                       
       01 WS-USER-BALANCE PIC S9(15)V9(2) COMP-3.                       
       01 WS-BALANCE-DISPLAY PIC ZZZ,ZZZ,ZZZ,ZZZ,999.99.                
       01 WS-TO-ID PIC S9(9) COMP VALUE ZERO.                           
       01 WS-COMMAREA.                                                  
         05 WS-USER-ID PIC S9(9) COMP VALUE ZERO.                       
         05 WS-USER-EMAIL PIC X(50).                                    
         05 WS-FIRST-TIME PIC X.                                        
       01 WS-END PIC X(3) VALUE "END".                                  
       01 WS-SQL-CODE PIC -9(6).                                        
       LINKAGE SECTION.                                                 
       01 DFHCOMMAREA.                                                  
         05 LS-USER-ID PIC S9(9) COMP.                                  
         05 LS-USER-EMAIL PIC X(50).                                    
         05 LS-FIRST-TIME PIC X.                                        
       PROCEDURE DIVISION.                                              
       MAIN.                                                            
           IF EIBCALEN = ZERO THEN                                      
              PERFORM REDIRECT-LOGIN                                    
           ELSE                                                         
              MOVE DFHCOMMAREA TO WS-COMMAREA                           
              IF WS-FIRST-TIME = 'Y' THEN                               
                 PERFORM INIT-PROGRAM                                   
                 MOVE 'N' TO WS-FIRST-TIME                              
                 PERFORM SEND-SCREEN                                    
              ELSE                                                      
                 PERFORM RECEIVE-SCREEN                                 
              END-IF                                                    
           END-IF.                                                      
       REDIRECT-LOGIN.                                                  
           EXEC CICS XCTL                                               
              PROGRAM('LOGINM')                                         
           END-EXEC.                                                    
       REDIRECT-PROFILE.                                                
           MOVE 'Y' TO WS-FIRST-TIME                                    
           EXEC CICS XCTL                                               
              PROGRAM('PROFM')                                          
              COMMAREA(WS-COMMAREA)                                     
              LENGTH(55)                                                
           END-EXEC.                                                    
       INIT-PROGRAM.                                                    
           MOVE LOW-VALUES TO SCRN1I.                                   
       RECEIVE-SCREEN.                                                  
           EXEC CICS RECEIVE                                            
               MAPSET('TFMP')                                           
              MAP('SCRN1')                                              
              INTO(SCRN1I)                                              
           END-EXEC                                                     
           PERFORM GET-BALANCE                                          
           PERFORM CHECK-KEY.                                           
       CHECK-KEY.                                                       
           EVALUATE TRUE                                                
              WHEN EIBAID = DFHENTER                                    
                PERFORM EVALUATE-DATA                                   
              WHEN EIBAID = DFHPF3                                      
                MOVE 'Y' TO WS-FIRST-TIME                               
                PERFORM REDIRECT-PROFILE                                
             WHEN OTHER                                                 
                MOVE 'INVALID KEY' TO WS-MSG                            
                MOVE WS-MSG TO MSGO                                     
                PERFORM SEND-SCREEN                                     
           END-EVALUATE.                                                
       EVALUATE-DATA.                                                   
           IF AMOUNTL = 0 THEN                                          
             MOVE 'YOU MUST PROVIDE AN AMOUNT' TO WS-MSG                
             PERFORM SEND-SCREEN                                        
           END-IF                                                       
                                                                        
           IF ACCIDL = 0 THEN                                           
             MOVE 'YOU MUST PROVIDE THE ACCOUNT ID' TO WS-MSG           
             PERFORM SEND-SCREEN                                        
           END-IF                                                       
                                                                        
           MOVE AMOUNTI TO WS-USER-BALANCE                              
           MOVE ACCIDI TO WS-TO-ID                                      
      *    CHECK IF ACCIDI EXISTS ON DB                                 
           EXEC SQL                                                     
              SELECT USER_ID                                            
              INTO :WS-TO-ID                                            
              FROM USERS                                                
              WHERE USER_ID = :WS-TO-ID                                 
           END-EXEC                                                     
                                                                        
           IF SQLCODE = 100 THEN                                        
              MOVE 'NO ACCOUNT FOUND' TO WS-MSG                         
              PERFORM SEND-SCREEN                                       
           END-IF                                                       
                                                                        
           MOVE 'ACCOUNT FOUND' TO WS-MSG                               
           COMPUTE WS-USER-BALANCE = WS-USER-BALANCE * -1               
           COMPUTE DB-USER-BALANCE = DB-USER-BALANCE * -1               
           COMPUTE DB-USER-BALANCE = DB-USER-BALANCE - WS-USER-BALANCE  
                                                                        
           IF DB-USER-BALANCE  <  0 THEN                                
             MOVE 'INSUFFICIENT FUNDS' TO WS-MSG                        
             PERFORM SEND-SCREEN                                        
           END-IF                                                       
                                                                        
           EXEC SQL                                                     
             UPDATE ACCOUNTS_NEW                                        
             SET BALANCE = BALANCE - :WS-USER-BALANCE                   
             WHERE USER_ID = :WS-USER-ID                                
           END-EXEC                                                     
                                                                        
           IF SQLCODE NOT = 0 THEN                                      
             MOVE 'NOT ABLE TO WITHDRAW FROM ACCOUNT' TO WS-MSG         
             PERFORM SEND-SCREEN                                        
           END-IF                                                       
                                                                        
           EXEC SQL                                                     
             UPDATE ACCOUNTS_NEW                                        
             SET BALANCE = BALANCE + :WS-USER-BALANCE                   
             WHERE USER_ID = :WS-TO-ID                                                                                                          
           END-EXEC                                                     
                                                                        
           IF SQLCODE NOT = 0 THEN                                      
             MOVE 'COULD NOT TRANSFER TO ACCOUNT' TO WS-MSG             
             PERFORM SEND-SCREEN                                        
           END-IF                                                       
                                                                        
           PERFORM REDIRECT-PROFILE.                                    
       SEND-SCREEN.                                                     
           MOVE WS-MSG TO MSGO                                          
           MOVE WS-USER-EMAIL TO UMAILO                                 
           EXEC CICS SEND                                               
              MAPSET('TFMP')                                            
              MAP('SCRN1')                                              
              FROM(SCRN1O)                                              
              ERASE                                                     
              FREEKB                                                    
           END-EXEC                                                     
           EXEC CICS RETURN                                             
             TRANSID(WS-TRANSID)                                        
             COMMAREA(WS-COMMAREA)                                      
             LENGTH(55)                                                 
           END-EXEC.                                                    
       GET-BALANCE.                                                     
           EXEC SQL                                                     
             SELECT BALANCE                                             
             INTO  :DB-USER-BALANCE                                     
             FROM ACCOUNTS_NEW                                          
             WHERE USER_ID = :WS-USER-ID                                
           END-EXEC                                                     
                                                                        
           MOVE WS-USER-ID TO WS-USER-ID-DISPLAY                        
                                                                        
           IF SQLCODE NOT = 0 THEN                                      
              MOVE SQLCODE TO WS-SQL-CODE                               
              STRING 'ERROR:' DELIMITED BY SIZE                         
                    WS-SQL-CODE DELIMITED BY SIZE                       
                    'ID=' DELIMITED BY SIZE                             
                    WS-USER-ID-DISPLAY DELIMITED BY SIZE                
                    INTO WS-MSG                                         
              END-STRING                                                
           END-IF                                                       
                                                                        
           IF SQLCODE = 0 THEN                                          
             MOVE DB-USER-BALANCE TO WS-BALANCE-DISPLAY                 
             MOVE WS-BALANCE-DISPLAY TO BALNCO                          
           END-IF.                                                      
       EXIT-PROGRAM.                                                    
           EXEC CICS                                                    
             SEND CONTROL                                               
             ERASE                                                      
             FREEKB                                                     
           END-EXEC                                                     
           EXEC CICS RETURN                                             
           END-EXEC.                                                                                                                                                          