       IDENTIFICATION DIVISION.                                         
       PROGRAM-ID. TEMPTL.                                              
      ***************************************************************** 
      * ARAT00: AIRCRAFT MENU CONTROLLER                                
      *                                                                 
      ***************************************************************** 
       ENVIRONMENT DIVISION.                                            
       DATA DIVISION.                                                   
       WORKING-STORAGE SECTION.                                         
       COPY DFHAID.                                                     
       COPY MPAT00.                                                     
                                                                        
       01 WS-COMMAREA.                                                  
          COPY ARCOMMA.                                                 
       01 WS-CICS.                                                      
          05 WS-CICS-TRANSID PIC X(4) VALUE "AT00".                     
          05 WS-CICS-MAPSET PIC X(10) VALUE "MPAT00".                   
          05 WS-CICS-MAP PIC X(10) VALUE "SCRN1".                       
          05 WS-CICS-CALEN PIC 9(2) VALUE 1.                            
       01 WS-OUT.                                                       
          05 WS-MSG PIC X(40) VALUE SPACES.                             
       01 WS-IN.                                                        
          05 WS-OPT PIC 9(2).                                           
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
                 PERFORM RETURN-MENU                                    
              WHEN OTHER                                                
                 PERFORM INVALID-KEY-PARA                               
           END-EVALUATE.                                                
       INVALID-KEY-PARA.                                                
           MOVE 'INVALID KEY' TO WS-MSG                                 
           PERFORM SEND-SCREEN-PARA.                                    
       PROCESS-PARA.                                                    
           IF AT00OPI = SPACES THEN                                     
              MOVE 'YOU MUST SELECT AN OPTION' TO WS-MSG                
              PERFORM SEND-SCREEN-PARA                                  
           END-IF                                                       
           IF AT00OPI NOT NUMERIC THEN                                  
              MOVE 'IT MUST BE A NUMBER' TO WS-MSG                      
           ELSE                                                         
              MOVE AT00OPI TO WS-OPT                                    
              IF WS-OPT <= 0 OR WS-OPT >= 3 THEN                        
                 STRING 'OPTION OUT OF RANGE: ' DELIMITED BY SIZE       
                        WS-OPT  DELIMITED BY SIZE                       
                        INTO WS-MSG                                     
                 END-STRING                                             
              END-IF                                                    
           END-IF                                                       
                                                                        
           IF WS-MSG NOT = SPACES THEN                                  
              PERFORM SEND-SCREEN-PARA                                  
           ELSE                                                         
              PERFORM EVALUATE-OP-PARA                                 
           END-IF.                                                     
       EVALUATE-OP-PARA.                                               
           EVALUATE WS-OPT                                             
              WHEN 1                                                   
                 EXEC CICS XCTL PROGRAM('ARAT01')                      
                 END-EXEC                                              
              WHEN 2                                                   
                 EXEC CICS XCTL PROGRAM('ARAT02')                      
                 END-EXEC                                              
              WHEN OTHER                                               
                 PERFORM SEND-SCREEN-PARA                              
           END-EVALUATE.                                               
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
       RETURN-MENU.                                                     
           EXEC CICS XCTL PROGRAM('AR00')                               
           END-EXEC.                                                    
       EXIT-PARA.                                                       
           EXEC CICS                                                    
              SEND CONTROL                                              
              ERASE                                                     
              FREEKB                                                    
           END-EXEC                                                     
                                                                        
           EXEC CICS RETURN                                             
           END-EXEC.                                                    