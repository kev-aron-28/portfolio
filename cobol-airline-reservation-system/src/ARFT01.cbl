       IDENTIFICATION DIVISION.                                       
       PROGRAM-ID. ARFT01.                                            
      ****************************************************************
      * ARFT01: FLIGHT INQUIRY                                        
      *                                                               
      ****************************************************************
       ENVIRONMENT DIVISION.                                          
       DATA DIVISION.                                                 
       WORKING-STORAGE SECTION.                                       
           EXEC SQL                                                   
              INCLUDE SQLCA                                           
           END-EXEC.                                                  
       COPY DFHAID.                                                   
       COPY MPFT01.                                                   
                                                                      
       01 WS-COMMAREA.                                                
          COPY ARCOMMA.                                               
       01 WS-CICS.                                                      
          05 WS-CICS-TRANSID PIC X(4) VALUE "FT01".                     
          05 WS-CICS-MAPSET PIC X(10) VALUE "MPFT01".                   
          05 WS-CICS-MAP PIC X(10) VALUE "SCRN1".                       
          05 WS-CICS-CALEN PIC 9(2) VALUE 1.                            
       01 WS-OUT.                                                       
          05 WS-MSG PIC X(40).                                          
          05 WS-SQL-CODE PIC -9(6).                                     
          05 WS-CAPACITY-DISP PIC 9(9).                                 
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
          05 WS-ORIGIN-CODE PIC X(3).                                   
          05 WS-DESTINATION-CODE PIC X(3).                              
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
           IF FT01NMI = SPACES THEN                                     
              MOVE 'YOU MUST PROVIDE THE FLIGHT NUMBER' TO WS-MSG       
              PERFORM SEND-SCREEN-PARA                                  
           END-IF                                                       
                                                                        
           MOVE FT01NMI TO WS-FLIGHT-NUM                                
                                                                        
           EXEC SQL                                                     
              SELECT                                                    
                 F.FLIGHT_ID,                                           
                 F.AIRCRAFT_ID,                                         
                 AO.CODE AS ORIGIN,                                     
                 AD.CODE AS DESTINATION,                                
                 F.DEPARTURE_TIME,                                      
                 F.ARRIVAL_TIME,                                        
                 F.STATUS                                               
              INTO                                                      
                 :WS-ID,                                                
                 :WS-AIRCRAFT-ID,                                       
                 :WS-ORIGIN-CODE,                                       
                 :WS-DESTINATION-CODE,                                  
                 :WS-DEPARTURE-TS,                                      
                 :WS-ARRIVAL-TS,                                        
                 :WS-STATUS                                             
              FROM FLIGHT F                                             
              INNER JOIN AIRPORT AO                                     
                 ON F.ORIGIN_ID = AO.AIRPORT_ID                         
              INNER JOIN AIRPORT AD                                     
                 ON F.DESTINATION_ID = AD.AIRPORT_ID                    
              WHERE F.FLIGHT_NUMBER = :WS-FLIGHT-NUM                    
           END-EXEC                                                     
                                                                        
           IF SQLCODE = 100 THEN                                        
              MOVE 'NO FLIGHT FOUND' TO WS-MSG                          
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
                                                                        
           MOVE WS-ID TO FT01IDO                                        
           MOVE WS-AIRCRAFT-ID TO FT01AIO                               
           MOVE WS-ORIGIN-CODE TO FT01ORO                               
           MOVE WS-DESTINATION-CODE TO FT01DEO                          
           MOVE WS-STATUS TO FT01STO                                    
                                                                        
           STRING WS-DEP-YEAR DELIMITED BY SIZE                         
                  '-' DELIMITED BY SIZE                                 
                  WS-DEP-MONTH DELIMITED BY SIZE                        
                  '-' DELIMITED BY SIZE                                 
                  WS-DEP-DAY DELIMITED BY SIZE                          
                  INTO FT01DDO                                          
           END-STRING                                                   
                                                                        
           STRING WS-DEP-HOUR DELIMITED BY SIZE                         
                 '.' DELIMITED BY SIZE                                  
                 WS-DEP-MINUTE DELIMITED BY SIZE                        
                 INTO FT01DTO                                           
           END-STRING                                                   
                                                                        
           STRING WS-ARR-YEAR DELIMITED BY SIZE                         
                  '-' DELIMITED BY SIZE                                 
                  WS-ARR-MONTH DELIMITED BY SIZE                        
                  '-' DELIMITED BY SIZE                                 
                  WS-ARR-DAY DELIMITED BY SIZE                          
                  INTO FT01ADO                                          
           END-STRING                                                   
                                                                        
           STRING WS-ARR-HOUR DELIMITED BY SIZE                         
                 '.' DELIMITED BY SIZE                                  
                 WS-ARR-MINUTE DELIMITED BY SIZE                        
                 INTO FT01ATO                                           
           END-STRING                                                   
           MOVE 'FLIGHT FOUND' TO WS-MSG                                
                                                                        
           PERFORM SEND-SCREEN-PARA.                                    
       SEND-SCREEN-PARA.                                                
           MOVE WS-MSG TO MSGO                                          
                                                                        
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
       EXIT-PARA.                                                       
           EXEC CICS                                                    
              SEND CONTROL                                              
              ERASE                                                     
              FREEKB                                                    
           END-EXEC                                                     
                                                                        
           EXEC CICS RETURN                                             
           END-EXEC.                                                    