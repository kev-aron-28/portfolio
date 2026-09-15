       IDENTIFICATION DIVISION.                                         
       PROGRAM-ID. ARAP01.                                              
      ***************************************************************** 
      * ARAP01: AIRPORT INQUIRY                                         
      *                                                                 
      ***************************************************************** 
       ENVIRONMENT DIVISION.                                            
       DATA DIVISION.                                                   
       WORKING-STORAGE SECTION.                                         
           EXEC SQL                                                     
              INCLUDE SQLCA                                             
           END-EXEC.                                                    
       COPY DFHAID.                                                     
       COPY MPAP01.                                                     
                                                                        
       01 WS-COMMAREA.                                                  
          COPY ARCOMMA.                                                 
       01 WS-CICS.                                                      
          05 WS-CICS-TRANSID PIC X(4) VALUE "AP01".                     
          05 WS-CICS-MAPSET PIC X(10) VALUE "MPAP01".                   
          05 WS-CICS-MAP PIC X(10) VALUE "SCRN1".                       
          05 WS-CICS-CALEN PIC 9(2) VALUE 1.                            
       01 WS-OUT.                                                       
          05 WS-MSG PIC X(40).                                          
          05 WS-SQL-CODE PIC -9(6).                                     
       01 WS-ID PIC S9(9) COMP.                                         
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
           IF AP01IDI = LOW-VALUES OR AP01IDI = SPACES THEN             
              MOVE 'YOU MUST PROVIDE THE ID' TO WS-MSG                  
              PERFORM SEND-SCREEN-PARA                                  
           END-IF                                                       
                                                                        
           IF AP01IDI NOT NUMERIC THEN                                  
              MOVE 'ID MUST BE NUMERIC' TO WS-MSG                       
              PERFORM SEND-SCREEN-PARA                                  
           END-IF                                                       
                                                                        
           MOVE AP01IDI TO WS-ID                                        
                                                                        
           EXEC SQL                                                     
              SELECT CODE,NAME,CITY,COUNTRY                             
              INTO                                                      
              :WS-CODE,                                                 
              :WS-NAME,                                                 
              :WS-CITY,                                                 
              :WS-COUNTRY                                               
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
                                                                        
           MOVE WS-CODE TO AP01CDO                                      
           MOVE WS-NAME TO AP01NAO                                      
           MOVE WS-CITY TO AP01CYO                                      
           MOVE WS-COUNTRY TO AP01CRO                                   
                                                                        
           MOVE 'AIRPORT FOUND' TO WS-MSG                               
                                                                        
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