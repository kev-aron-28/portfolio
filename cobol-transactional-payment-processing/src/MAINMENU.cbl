       IDENTIFICATION DIVISION.                                         
       PROGRAM-ID. MAINMENU.                                            
       ENVIRONMENT DIVISION.                                            
       DATA DIVISION.                                                   
       WORKING-STORAGE SECTION.                                         
       COPY DFHAID.                                                     
       COPY MAIN.                                                       
       01 WS-TRANSID PIC X(4) VALUE 'BANK'.                             
       01 WS-COMMAREA.                                                  
         05 WS-CONTINUE PIC X VALUE SPACES.                             
       01 WS-MSG PIC X(40) VALUE SPACES.                                
       01 WS-OPT PIC 9(2).                                              
       01 WS-END PIC X(3) VALUE "FIN".                                  
       LINKAGE SECTION.                                                 
       01 DFHCOMMAREA.                                                  
         05 WS-CONTINUE PIC X.                                          
       PROCEDURE DIVISION.                                              
       MAIN.                                                            
      * FIRST TIME                                                      
           IF EIBCALEN = ZERO THEN                                      
              PERFORM INIT-PROGRAM                                      
              PERFORM FIRST-TIME                                        
           ELSE                                                         
              PERFORM RECEIVE-SCREEN                                    
           END-IF.                                                      
       INIT-PROGRAM.                                                    
           MOVE LOW-VALUES TO SCRN1I.                                   
       FIRST-TIME.                                                      
           EXEC CICS SEND                                               
              MAPSET('MAIN')                                            
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
             MAPSET('MAIN')                                           
             MAP('SCRN1')                                             
             INTO(SCRN1I)                                             
          END-EXEC                                                    
          PERFORM CHECK-KEY.                                          
      CHECK-KEY.                                                      
          EVALUATE EIBAID                                             
             WHEN DFHENTER                                            
               PERFORM EVALUATE-DATA                                  
             WHEN DFHPF3                                              
               PERFORM EXIT-PROGRAM                                   
             WHEN OTHER                                               
               MOVE 'INVALID KEY' TO WS-MSG                           
               MOVE WS-MSG TO MSGO                                    
               PERFORM SEND-SCREEN                                    
          END-EVALUATE.                                               
      EVALUATE-DATA.                                                  
           IF OPTL = 0 THEN                                            
              MOVE 'OPTION IS REQUIRED' TO WS-MSG                        
            ELSE IF OPTI NOT NUMERIC                                     
              MOVE 'MUST BE A NUMBER' TO WS-MSG                          
            ELSE                                                         
              MOVE OPTI TO WS-OPT                                        
              IF WS-OPT < 1 OR WS-OPT > 3 THEN                           
                MOVE 'OUT OF RANGE' TO WS-MSG                            
              END-IF                                                     
            END-IF                                                       
            MOVE WS-MSG TO MSGO                                          
      *  EVALUATE IF THERE IS AN ERROR MESSAGE                          
            IF WS-MSG NOT = SPACES                                       
              PERFORM SEND-SCREEN                                        
            ELSE                                                         
              PERFORM EVALUATE-OPTION                                    
            END-IF.                                                      
        EVALUATE-OPTION.                                                 
            EVALUATE WS-OPT                                              
              WHEN 3                                                     
               PERFORM EXIT-PROGRAM                                     
             WHEN OTHER                                                 
               PERFORM SEND-SCREEN                                      
           END-EVALUATE.                                                
       SEND-SCREEN.                                                     
           EXEC CICS SEND                                               
              MAPSET('MAIN')                                            
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
              SEND TEXT FROM(WS-END)                                    
              LENGTH(3)                                                 
              ERASE                                                     
              FREEKB                                                    
           END-EXEC                                                     
           EXEC CICS RETURN                                             
           END-EXEC.                                                    