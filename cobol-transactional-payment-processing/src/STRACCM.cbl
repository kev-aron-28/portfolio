       IDENTIFICATION DIVISION.                                         
       PROGRAM-ID. NEWACCM.                                             
      ******************************************************************
      * START ACCOUNT WITH BALANCE CONTROLLER                           
      ******************************************************************
       ENVIRONMENT DIVISION.                                            
       DATA DIVISION.                                                   
       WORKING-STORAGE SECTION.                                         
           EXEC SQL                                                     
            INCLUDE SQLCA                                               
           END-EXEC.                                                    
       COPY DFHAID.                                                     
       COPY SCMP.                                                       
      * THE TRANSID IS BANK START ACCOUNT (BSA)                         
       01 WS-TRANSID PIC X(4) VALUE "BSA".                              
       01 WS-MSG PIC X(40) VALUE SPACES.                                
       01 WS-USER-INPUT.                                                
         05 WS-USER-BALANCE PIC S9(15)V9(2) COMP-3.                     
       01 WS-USER-ID-DISPLAY PIC ZZZZZZZZ9.                             
       01 WS-COMMAREA.                                                  
         05 WS-USER-NAME PIC X(50) VALUE SPACES.                        
         05 WS-FIRST-TIME PIC X VALUE 'Y'.                              
       01 WS-COMMAREA-PROFILE.                                          
         05 WS-USER-ID PIC S9(9) COMP VALUE ZERO.                       
         05 WS-USER-EMAIL PIC X(50).                                    
         05 WS-FIRST-TIME PIC X VALUE 'Y'.                              
       01 WS-END PIC X(3) VALUE "END".                                  
       01 WS-SQL-CODE PIC -9(6).                                        
       LINKAGE SECTION.                                                 
       01 DFHCOMMAREA.                                                  
         05 LS-USER-NAME PIC X(50).                                     
         05 LS-FIRST-TIME PIC X.                                        
                                                                        
       PROCEDURE DIVISION.                                              
       MAIN.                                                            
      * CHECK IF ITS THE FIRST TIME                                     
           IF EIBCALEN = ZERO THEN                                      
              PERFORM REDIRECT-LOGIN                                    
           ELSE                                                         
              MOVE DFHCOMMAREA TO WS-COMMAREA                           
              MOVE WS-USER-NAME TO WS-USER-EMAIL                        
              IF WS-FIRST-TIME OF WS-COMMAREA = 'Y' THEN                
                 MOVE 'N' TO WS-FIRST-TIME OF WS-COMMAREA               
                 PERFORM FIRST-TIME                                     
              ELSE                                                      
                 PERFORM RECEIVE-SCREEN                                 
              END-IF                                                    
           END-IF.                                                      
       REDIRECT-LOGIN.                                                  
           EXEC CICS XCTL                                               
              PROGRAM('LOGINM')                                         
           END-EXEC.                                                    
       FIRST-TIME.                                                      
           MOVE LOW-VALUES TO SCRN1I                                    
           STRING 'WELCOME ' DELIMITED BY SIZE                          
             WS-USER-NAME DELIMITED BY SIZE                             
             INTO MSGO                                                                       
           END-STRING                                                   
           PERFORM SEND-SCREEN.                                         
       RECEIVE-SCREEN.                                                  
           EXEC CICS RECEIVE                                            
              MAPSET('SCMP')                                            
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
      * EVALUATE QUANTITY IS >= 0                                       
           IF AMOUNTI < 0 THEN                                          
              MOVE 'QUANTITY MUST BE >= 0' TO WS-MSG                    
              PERFORM SEND-SCREEN                                       
           END-IF                                                       
           MOVE AMOUNTI TO WS-USER-BALANCE                              
           EXEC SQL                                                     
             SELECT USER_ID INTO :WS-USER-ID                            
             FROM USERS                                                 
             WHERE EMAIL = :WS-USER-NAME                                
           END-EXEC                                                     
      * NO DATA FOUND                                                   
           IF SQLCODE = 100 THEN                                        
             STRING 'NO USER FOUND:' DELIMITED BY SIZE                  
                    WS-USER-NAME DELIMITED BY SIZE                      
               INTO WS-MSG                                              
             END-STRING                                                 
             PERFORM SEND-SCREEN                                        
           END-IF                                                       
           PERFORM CHECK-SQL-ERROR                                      
      * CREATE NEW ACCOUNT WITH STARTING BALANCE                        
           EXEC SQL                                                     
             INSERT INTO ACCOUNTS_NEW                                   
             (USER_ID,BALANCE)                                          
             VALUES(:WS-USER-ID,:WS-USER-BALANCE)                       
           END-EXEC                                                     
           PERFORM CHECK-SQL-ERROR                                      
      * SEND TO PROFILE VIEW                                            
           EXEC CICS XCTL                                               
             PROGRAM('PROFM')                                           
             COMMAREA(WS-COMMAREA-PROFILE)                              
             LENGTH(55)                                                 
           END-EXEC.                                                    
       CHECK-SQL-ERROR.                                                 
           IF SQLCODE < 0 THEN                                                                                                      
             MOVE SQLCODE TO WS-SQL-CODE                                
             STRING 'SQL ERROR:' DELIMITED BY SIZE                      
                    WS-SQL-CODE DELIMITED BY SIZE                       
                    INTO WS-MSG                                         
             END-STRING                                                 
             PERFORM SEND-SCREEN                                        
           END-IF.                                                      
       SEND-SCREEN.                                                     
           MOVE WS-MSG TO MSGO                                          
                                                                        
           EXEC CICS SEND                                               
              MAPSET('SCMP')                                            
              MAP('SCRN1')                                              
              FROM(SCRN1O)                                              
              ERASE                                                     
              FREEKB                                                    
           END-EXEC                                                     
                                                                        
           EXEC CICS RETURN                                                        
              TRANSID(WS-TRANSID)                                       
              COMMAREA(WS-COMMAREA)                                     
              LENGTH(51)                                                
           END-EXEC.                                                    
       EXIT-PROGRAM.                                                    
           EXEC CICS                                                    
             SEND CONTROL                                               
             ERASE                                                      
             FREEKB                                                     
           END-EXEC                                                     
           EXEC CICS RETURN                                             
           END-EXEC.                                                               