       IDENTIFICATION DIVISION.                                         
       PROGRAM-ID. ARFT02.                                              
      ***************************************************************** 
      * ARFT02: CREATE FLIGHT                                           
      *                                                                 
      ***************************************************************** 
       ENVIRONMENT DIVISION.                                            
       DATA DIVISION.                                                   
       WORKING-STORAGE SECTION.                                         
           EXEC SQL                                                     
              INCLUDE SQLCA                                             
           END-EXEC.                                                    
       COPY DFHAID.                                                     
       COPY MPFT02.                                                     
                                                                        
       01 WS-COMMAREA.                                                  
          COPY ARCOMMA.                                                 
       01 WS-CICS.                                                      
          05 WS-CICS-TRANSID PIC X(4) VALUE "FT02".                     
          05 WS-CICS-MAPSET PIC X(10) VALUE "MPFT02".                   
          05 WS-CICS-MAP PIC X(10) VALUE "SCRN1".                       
          05 WS-CICS-CALEN PIC 9(2) VALUE 1.                            
       01 WS-OUT.                                                       
          05 WS-MSG PIC X(40).                                          
          05 WS-SQL-CODE PIC -9(6).                                     
       01 WS-DEPARTURE-TS PIC X(26).                                    
       01 WS-DEPARTURE-TS-R REDEFINES WS-DEPARTURE-TS.                  
          05 WS-DEP-YEAR PIC X(4).                                      
          05 FILLER PIC X.                                              
          05 WS-DEP-MONTH PIC X(2).                                     
          05 FILLER PIC X.                                              
          05 WS-DEP-DAY PIC X(2).                                       
          05 FILLER PIC X.                                              
          05 WS-DEP-HOUR PIC X(2).                                      
          05 FILLER PIC X.                                              
          05 WS-DEP-MINUTE PIC X(2).                                    
          05 FILLER PIC X.                                              
          05 WS-DEP-SECOND PIC X(2).                                    
          05 FILLER PIC X.                                              
          05 WS-DEP-MICRO PIC X(6).                                     
       01 WS-ARRIVAL-TS PIC X(26).                                      
       01 WS-ARRIVAL-TS-R REDEFINES WS-ARRIVAL-TS.                      
          05 WS-ARR-YEAR PIC X(4).                                      
          05 FILLER PIC X.                                              
          05 WS-ARR-MONTH PIC X(2).                                     
          05 FILLER PIC X.                                              
          05 WS-ARR-DAY PIC X(2).                                       
          05 FILLER PIC X.                                              
          05 WS-ARR-HOUR PIC X(2).                                      
          05 FILLER PIC X.                                              
          05 WS-ARR-MINUTE PIC X(2).                                    
          05 FILLER PIC X.                                              
          05 WS-ARR-SECOND PIC X(2).                                    
          05 FILLER PIC X.                                              
          05 WS-ARR-MICRO PIC X(6).                                     
       01 WS-FLIGHT.                                                    
          05 WS-ID PIC S9(9) COMP.                                      
          05 WS-AIRCRAFT-ID PIC S9(9) COMP.                             
          05 WS-FLIGHT-NUM PIC X(10).                                   
          05 WS-FLIGHT-SEQ-DISP PIC X(9).                               
          05 WS-FLIGHT-SEQ PIC S9(9) COMP.                              
          05 WS-ORIGIN-CODE PIC X(3).                                   
          05 WS-ORIGIN-ID PIC S9(9) COMP.                               
          05 WS-DESTINATION-CODE PIC X(3).                              
          05 WS-DESTINATION-ID PIC S9(9) COMP.                          
          05 WS-STATUS PIC X VALUE "A".                                 
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
           IF FT01AII = SPACES                                          
           OR FT01ORI = SPACES                                          
           OR FT01DEI = SPACES                                          
           OR FT01DDI = SPACES                                          
           OR FT01DTI = SPACES                                          
           OR FT01ADI = SPACES                                          
           OR FT01ATI = SPACES                                          
           OR FT01STI = SPACES                                          
           THEN                                                         
              MOVE 'YOU MUST PROVIDE ALL FIELDS' TO WS-MSG              
              PERFORM SEND-SCREEN-PARA                                  
           END-IF                                                       
                                                                        
                                                                        
           IF FT01AII IS NOT NUMERIC THEN                               
              MOVE 'AIRPLANE ID MUST BE NUMERIC' TO WS-MSG              
              PERFORM SEND-SCREEN-PARA                                  
           END-IF                                                       
                                                                        
           MOVE FT01AII TO WS-AIRCRAFT-ID                               
      * CHECK IF AIRCRAFT EXISTS                                        
           EXEC SQL                                                     
              SELECT AIRCRAFT_ID INTO :WS-AIRCRAFT-ID                   
              FROM AIRCRAFT                                             
              WHERE AIRCRAFT_ID = :WS-AIRCRAFT-ID                       
           END-EXEC                                                     
                                                                        
           IF SQLCODE = 100 THEN                                        
              MOVE 'NO AIRCRAFT FOUND' TO WS-MSG                        
              PERFORM SEND-SCREEN-PARA                                  
           END-IF                                                       
                                                                        
           PERFORM HANDLE-SQL-ERROR                                     
                                                                        
      * CHECK IF ORIGIN EXISTS                                          
           MOVE FT01ORI TO WS-ORIGIN-CODE                               
                                                                        
           EXEC SQL                                                     
              SELECT AIRPORT_ID INTO :WS-ORIGIN-ID                      
              FROM AIRPORT                                              
              WHERE CODE = :WS-ORIGIN-CODE                              
           END-EXEC                                                     
                                                                        
           IF SQLCODE = 100 THEN                                        
              MOVE 'THE ORIGIN DOES NOT EXIST' TO WS-MSG                
              PERFORM SEND-SCREEN-PARA                                  
           END-IF                                                       
                                                                        
           PERFORM HANDLE-SQL-ERROR                                     
                                                                        
           MOVE FT01DEI TO WS-DESTINATION-CODE                          
                                                                        
           EXEC SQL                                                     
              SELECT AIRPORT_ID INTO :WS-DESTINATION-ID                 
              FROM AIRPORT                                              
              WHERE CODE = :WS-DESTINATION-CODE                         
           END-EXEC                                                     
                                                                        
           IF SQLCODE = 100 THEN                                        
              MOVE 'THE DESTINATION DOES NOT EXIST' TO WS-MSG           
           END-IF                                                       
                                                                        
           PERFORM HANDLE-SQL-ERROR                                     
                                                                        
      * CHECK IF THE TIMESTAMPS ARE CORRECT                             
           PERFORM CHECK-DEPARTURE-TS                                   
                                                                        
           PERFORM CHECK-ARRIVAL-TS                                     
                                                                        
           EXEC SQL                                                     
              VALUES NEXT VALUE FOR FLIGHT_SEQ                          
              INTO :WS-FLIGHT-SEQ                                       
           END-EXEC                                                     
                                                                        
           MOVE WS-FLIGHT-SEQ TO WS-FLIGHT-SEQ-DISP                     
           MOVE WS-FLIGHT-SEQ-DISP TO WS-MSG                            
                                                                        
           STRING 'F' DELIMITED BY SIZE                                 
                  WS-FLIGHT-SEQ-DISP DELIMITED BY SIZE                  
                 INTO WS-FLIGHT-NUM                                     
           END-STRING                                                   
                                                                        
           EXEC SQL                                                     
              INSERT INTO FLIGHT                                        
              (                                                         
               FLIGHT_NUMBER,                                           
               AIRCRAFT_ID,                                             
               ORIGIN_ID,                                               
               DESTINATION_ID,                                          
               DEPARTURE_TIME,                                          
               ARRIVAL_TIME,                                            
               STATUS                                                   
              ) VALUES                                                  
              (                                                         
               :WS-FLIGHT-NUM,                                          
               :WS-AIRCRAFT-ID,                                         
               :WS-ORIGIN-ID,                                           
               :WS-DESTINATION-ID,                                      
               :WS-DEPARTURE-TS,                                        
               :WS-ARRIVAL-TS,                                          
               :WS-STATUS                                               
              )                                                         
           END-EXEC                                                     
                                                                        
           PERFORM HANDLE-SQL-ERROR                                     
                                                                        
           STRING 'FLIGHT NUMBER:' DELIMITED BY SIZE                    
                  WS-FLIGHT-NUM DELIMITED BY SIZE                       
               INTO WS-MSG                                              
           END-STRING                                                   
                                                                        
           PERFORM SEND-SCREEN-PARA.                                    
       CHECK-DEPARTURE-TS.                                              
                                                                        
           STRING FT01DDI DELIMITED BY SIZE                             
                  '-' DELIMITED BY SIZE                                 
                  FT01DTI DELIMITED BY SIZE                             
                  '00.00.000000' DELIMITED BY SIZE                      
                  INTO WS-DEPARTURE-TS                                  
           END-STRING.                                                
       CHECK-ARRIVAL-TS.                                                
                                                                        
           STRING FT01ADI DELIMITED BY SIZE                             
                  '-' DELIMITED BY SIZE                                 
                  FT01ATI DELIMITED BY SIZE                             
                  '.00.000000' DELIMITED BY SIZE                        
                  INTO WS-ARRIVAL-TS                                    
           END-STRING.                                                  
       SEND-SCREEN-PARA.                                                
           MOVE WS-MSG TO FTMSGO                                        
                                                                        
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
              PROGRAM('ARFT00')                                         
           END-EXEC.                                                    
       HANDLE-SQL-ERROR.                                                
           IF SQLCODE = -181 THEN                                       
              MOVE WS-DEPARTURE-TS TO WS-MSG                            
              PERFORM SEND-SCREEN-PARA                                  
           ELSE IF SQLCODE < 0 THEN                                     
              MOVE SQLCODE TO WS-SQL-CODE                               
              STRING 'SOMETHING WENT WRONG: ' DELIMITED BY SIZE         
                     WS-SQL-CODE DELIMITED BY SIZE                      
                INTO WS-MSG                                             
              END-STRING                                                
              PERFORM SEND-SCREEN-PARA                                  
           END-IF.                                                      
       EXIT-PARA.                                                       
           EXEC CICS                                                    
              SEND CONTROL                                              
              ERASE                                                     
              FREEKB                                                    
           END-EXEC                                                     
                                                                        
           EXEC CICS RETURN                                             
           END-EXEC.                                                    