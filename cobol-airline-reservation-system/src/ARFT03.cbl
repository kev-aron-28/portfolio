       IDENTIFICATION DIVISION.                                         
       PROGRAM-ID. ARFT03.                                              
      ***************************************************************** 
      * ARFT03: SEARCH FLIGHT                                           
      *                                                                 
      ***************************************************************** 
       ENVIRONMENT DIVISION.                                            
       DATA DIVISION.                                                   
       WORKING-STORAGE SECTION.                                         
           EXEC SQL                                                     
              INCLUDE SQLCA                                             
           END-EXEC.                                                    
       COPY DFHAID.                                                     
       COPY MPFT03.                                                     
       01 WS-COMMAREA.                                                  
          05 WS-ORIGIN PIC X(3).                                        
          05 WS-DESTINATION PIC X(3).                                   
          05 WS-DEPARTURE-DATE-START PIC X(26).                         
          05 WS-DEPARTURE-DATE-END PIC X(26).                           
          05 WS-START-ROW PIC S9(9) COMP VALUE 1.                       
          05 WS-END-ROW PIC S9(9) COMP VALUE 9.                         
       01 WS-CICS.                                                      
          05 WS-CICS-TRANSID PIC X(4) VALUE "FT03".                     
          05 WS-CICS-MAPSET PIC X(10) VALUE "MPFT03".                   
          05 WS-CICS-MAP PIC X(10) VALUE "SCRN1".                       
          05 WS-CICS-CALEN PIC 9(2) VALUE 1.                            
       01 WS-OUT.                                                       
          05 WS-MSG PIC X(40).                                          
          05 WS-SQL-CODE PIC -9(6).                                     
       01 WS-DEPARTURE-TS PIC X(26) VALUE SPACES.                       
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
       01 WS-FLIGHT.                                                    
          05 WS-FLIGHT-NUM PIC X(10).                                   
          05 WS-FLIGHT-ORIGIN PIC X(3).                                 
          05 WS-FLIGHT-DESTINATION PIC X(3).                            
          05 WS-FLIGHT-DEPARTURE PIC X(16).                             
          05 WS-FLIGHT-DEPARTURE-T PIC X(26).                           
          05 WS-FLIGHT-ARRIVAL PIC X(16).                               
          05 WS-FLIGHT-ARRIVAL-T PIC X(26).                             
          05 WS-FLIGHT-STATUS PIC X.                                    
       01 WS-ROW PIC 9(2) VALUE 1.                                      
           EXEC SQL                                                     
              DECLARE FLIGHT-CURSOR CURSOR FOR                          
                 WITH ORDEREDROWS AS (                                  
                 SELECT                                                 
                    ROW_NUMBER() OVER (ORDER BY F.FLIGHT_ID)            
                    AS ROW_NUM,                                         
                    F.FLIGHT_NUMBER,                                    
                    AO.CODE AS ORIGIN,                                  
                    AD.CODE AS DESTINATION,                             
                    F.DEPARTURE_TIME,                                   
                    F.ARRIVAL_TIME,                                     
                    F.STATUS                                            
                 FROM FLIGHT F                                          
                 INNER JOIN AIRPORT AO                                  
                    ON F.ORIGIN_ID = AO.AIRPORT_ID                      
                 INNER JOIN AIRPORT AD                                  
                    ON F.DESTINATION_ID = AD.AIRPORT_ID                 
                 WHERE                                                  
                 AO.CODE = :WS-ORIGIN                                   
                 OR                                                     
                 AD.CODE = :WS-DESTINATION                              
                 OR                                                     
                 (                                                      
                 F.DEPARTURE_TIME >= :WS-DEPARTURE-DATE-START           
                 AND F.DEPARTURE_TIME < :WS-DEPARTURE-DATE-END          
                 )                                                      
                 )                                                      
                 SELECT                                                 
                    FLIGHT_NUMBER,                                      
                    ORIGIN,                                             
                    DESTINATION,                                        
                    DEPARTURE_TIME,                                     
                    ARRIVAL_TIME,                                       
                    STATUS                                              
                 FROM ORDEREDROWS                                       
                 WHERE ROW_NUM  BETWEEN :WS-START-ROW AND :WS-END-ROW   
                 FOR FETCH ONLY                                         
           END-EXEC.                                                    
       LINKAGE SECTION.                                                 
       01 DFHCOMMAREA.                                                  
          05 L-ORIGIN PIC X(3).                                         
          05 L-DESTINATION PIC X(3).                                    
          05 L-DEPARTURE-DATE-START PIC X(26).                          
          05 L-DEPARTURE-DATE-END PIC X(26).                            
          05 L-START-ROW PIC S9(9).                                     
          05 L-END-ROW PIC S9(9).                                       
       PROCEDURE DIVISION.                                              
       MAIN-PARA.                                                       
           IF EIBCALEN = 0 THEN                                         
              PERFORM INIT-PARA                                         
              PERFORM FIRST-TIME-PARA                                   
           ELSE                                                         
              MOVE DFHCOMMAREA TO WS-COMMAREA                           
              PERFORM RECEIVE-PARA                                      
           END-IF.                                                      
       INIT-PARA.                                                       
           MOVE LOW-VALUES TO SCRN1I                                    
           MOVE SPACES TO WS-ORIGIN                                     
           MOVE SPACES TO WS-DEPARTURE-DATE-START                       
           MOVE SPACES TO WS-DEPARTURE-DATE-END                         
           MOVE SPACES TO WS-DEPARTURE-TS                               
           MOVE 1 TO WS-START-ROW                                       
           MOVE 9 TO WS-END-ROW                                         
           MOVE SPACES TO WS-MSG                                        
           PERFORM CLEAN-ROWS-PARA.                                     
       FIRST-TIME-PARA.                                                 
           EXEC CICS SEND                                               
              MAPSET(WS-CICS-MAPSET)                                    
              MAP('SCRN1')                                              
              FREEKB                                                    
              ERASE                                                     
           END-EXEC                                                     
           EXEC CICS RETURN                                             
              TRANSID(WS-CICS-TRANSID)                                  
              COMMAREA(WS-COMMAREA)                                     
              LENGTH(66)                                                
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
              WHEN EIBAID = DFHPF8                                   
                 PERFORM NEXT-PAGE-PARA                              
              WHEN EIBAID = DFHPF7                                   
                 PERFORM PREV-PAGE-PARA                              
              WHEN OTHER                                             
                 PERFORM INVALID-KEY-PARA                            
           END-EVALUATE.                                             
       INVALID-KEY-PARA.                                             
           MOVE 'INVALID KEY' TO WS-MSG                              
           PERFORM SEND-SCREEN-PARA.                                 
       NEXT-PAGE-PARA.                                               
           ADD 9 TO WS-START-ROW                                     
           ADD 9 TO WS-END-ROW                                       
           PERFORM SEARCH-FLIGHT-PARA.                               
       PREV-PAGE-PARA.                                               
           IF WS-START-ROW > 1 THEN                                  
              SUBTRACT 9 FROM WS-START-ROW                           
              SUBTRACT 9 FROM WS-END-ROW                                
           ELSE                                                         
              MOVE 'CANNOT GO PREV' TO WS-MSG                           
              PERFORM SEND-SCREEN-PARA                                  
           END-IF.                                                      
       PROCESS-PARA.                                                    
           IF                                                           
            (ORGI = SPACES OR ORGI = LOW-VALUES) AND                    
            (DSTI = SPACES OR DSTI = LOW-VALUES) AND                    
            (DPTI = SPACES OR DPTI = LOW-VALUES)                        
           THEN                                                         
              MOVE 'YOU MUST PROVIDE AT LEAST ONE PARAMETER FOR SEARCH' 
                   TO WS-MSG                                            
              PERFORM SEND-SCREEN-PARA                                  
           END-IF                                                       
                                                                        
           MOVE SPACES TO WS-MSG                                        
           IF ORGI = LOW-VALUES OR ORGI = SPACES THEN                   
              MOVE SPACES TO WS-ORIGIN                                  
           ELSE                                                         
              MOVE ORGI TO WS-ORIGIN                                    
           END-IF                                                       
                                                                        
           IF DSTI = LOW-VALUES OR DSTI = SPACES THEN                   
              MOVE SPACES TO WS-DESTINATION                             
           ELSE                                                         
              MOVE DSTI TO WS-DESTINATION                               
           END-IF                                                       
                                                                        
           IF DPTI = LOW-VALUES OR DPTI = SPACES THEN                   
              MOVE '0001-01-01-00.00.00.000000' TO                      
                    WS-DEPARTURE-DATE-START                             
              MOVE '9999-12-31-23.59.59.999999' TO                      
                    WS-DEPARTURE-DATE-END                               
           ELSE                                                         
              MOVE DPTI TO WS-DEPARTURE-TS                              
              STRING WS-DEP-YEAR DELIMITED BY SIZE                      
                  '-' DELIMITED BY SIZE                                 
                  WS-DEP-MONTH DELIMITED BY SIZE                        
                  '-' DELIMITED BY SIZE                                 
                  WS-DEP-DAY DELIMITED BY SIZE                          
                  '-' DELIMITED BY SIZE                                 
                  '00.00.00.000000' DELIMITED BY SIZE                   
                  INTO WS-DEPARTURE-DATE-START                          
              END-STRING                                                
              STRING WS-DEP-YEAR DELIMITED BY SIZE                      
                  '-' DELIMITED BY SIZE                                 
                  WS-DEP-MONTH DELIMITED BY SIZE                        
                  '-' DELIMITED BY SIZE                                 
                  WS-DEP-DAY DELIMITED BY SIZE                          
                  '-' DELIMITED BY SIZE                                 
                  '23.59.59.999999' DELIMITED BY SIZE                   
                  INTO WS-DEPARTURE-DATE-END                            
              END-STRING                                                
           END-IF                                                       
                                                                        
           MOVE 1 TO WS-START-ROW                                       
           MOVE 9 TO WS-END-ROW                                         
                                                                        
           PERFORM SEARCH-FLIGHT-PARA.                                  
       SEARCH-FLIGHT-PARA.                                              
           PERFORM CLEAN-ROWS-PARA                                      
           EXEC SQL                                                     
              OPEN FLIGHT-CURSOR                                        
           END-EXEC                                                     
                                                                        
           IF SQLCODE NOT = 0 THEN                                      
              MOVE SQLCODE TO WS-MSG                                    
              PERFORM SEND-SCREEN-PARA                                  
           END-IF                                                       
                                                                        
           PERFORM VARYING WS-ROW FROM 1 BY 1                           
           UNTIL WS-ROW > 9                                             
              EXEC SQL                                                  
                 FETCH NEXT FROM FLIGHT-CURSOR                          
                 INTO                                                   
                    :WS-FLIGHT-NUM,                                     
                    :WS-FLIGHT-ORIGIN,                                  
                    :WS-FLIGHT-DESTINATION,                             
                    :WS-FLIGHT-DEPARTURE-T,                             
                    :WS-FLIGHT-ARRIVAL-T,                               
                    :WS-FLIGHT-STATUS                                   
              END-EXEC                                                  
                                                                        
              IF SQLCODE = 100 THEN                                     
                 MOVE 'NO MORE RESULTS' TO WS-MSG                       
                 PERFORM SEND-SCREEN-PARA                               
              END-IF                                                    
                                                                        
              IF SQLCODE < 0 THEN                                       
                 MOVE SQLCODE TO WS-MSG                                 
                 PERFORM SEND-SCREEN-PARA                               
              END-IF                                                    
                                                                        
              PERFORM MOVE-ROW-PARA                                     
           END-PERFORM                                                  
                                                                        
           EXEC SQL                                                     
              CLOSE FLIGHT-CURSOR                                       
           END-EXEC                                                     
                                                                        
           PERFORM SEND-SCREEN-PARA.                                    
       MOVE-ROW-PARA.                                                   
           IF WS-ROW = 1 THEN                                           
              STRING WS-FLIGHT-NUM DELIMITED BY SIZE                    
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-ORIGIN DELIMITED BY SIZE                     
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-DESTINATION DELIMITED BY SIZE                
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-DEPARTURE-T(1:16) DELIMITED BY SIZE          
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-ARRIVAL-T(1:16) DELIMITED BY SIZE            
                 '  ' DELIMITED BY SIZE                                 
                 WS-FLIGHT-STATUS DELIMITED BY SIZE                     
                 INTO ROW1O                                             
              END-STRING                                                
           END-IF                                                       
                                                                        
           IF WS-ROW = 2 THEN                                           
              STRING WS-FLIGHT-NUM DELIMITED BY SIZE                    
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-ORIGIN DELIMITED BY SIZE                     
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-DESTINATION DELIMITED BY SIZE                
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-DEPARTURE-T(1:16) DELIMITED BY SIZE          
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-ARRIVAL-T(1:16) DELIMITED BY SIZE            
                 '  ' DELIMITED BY SIZE                                 
                 WS-FLIGHT-STATUS DELIMITED BY SIZE                     
                 INTO ROW2O                                             
              END-STRING                                                
           END-IF                                                       
                                                                        
           IF WS-ROW = 3 THEN                                           
              STRING WS-FLIGHT-NUM DELIMITED BY SIZE                    
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-ORIGIN DELIMITED BY SIZE                     
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-DESTINATION DELIMITED BY SIZE                
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-DEPARTURE-T(1:16) DELIMITED BY SIZE          
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-ARRIVAL-T(1:16) DELIMITED BY SIZE            
                 '  ' DELIMITED BY SIZE                                 
                 WS-FLIGHT-STATUS DELIMITED BY SIZE                     
                 INTO ROW3O                                             
              END-STRING                                                
           END-IF                                                       
                                                                        
           IF WS-ROW = 4 THEN                                           
              STRING WS-FLIGHT-NUM DELIMITED BY SIZE                    
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-ORIGIN DELIMITED BY SIZE                     
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-DESTINATION DELIMITED BY SIZE                
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-DEPARTURE-T(1:16) DELIMITED BY SIZE          
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-ARRIVAL-T(1:16) DELIMITED BY SIZE            
                 '  ' DELIMITED BY SIZE                                 
                 WS-FLIGHT-STATUS DELIMITED BY SIZE                     
                 INTO ROW4O                                             
              END-STRING                                                
           END-IF                                                       
                                                                        
           IF WS-ROW = 5 THEN                                           
              STRING WS-FLIGHT-NUM DELIMITED BY SIZE                    
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-ORIGIN DELIMITED BY SIZE                     
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-DESTINATION DELIMITED BY SIZE                
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-DEPARTURE-T(1:16) DELIMITED BY SIZE          
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-ARRIVAL-T(1:16) DELIMITED BY SIZE            
                 '  ' DELIMITED BY SIZE                                 
                 WS-FLIGHT-STATUS DELIMITED BY SIZE                     
                 INTO ROW5O                                             
              END-STRING                                                
           END-IF                                                       
                                                                        
           IF WS-ROW = 6 THEN                                           
              STRING WS-FLIGHT-NUM DELIMITED BY SIZE                    
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-ORIGIN DELIMITED BY SIZE                     
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-DESTINATION DELIMITED BY SIZE                
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-DEPARTURE-T(1:16) DELIMITED BY SIZE          
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-ARRIVAL-T(1:16) DELIMITED BY SIZE            
                 '  ' DELIMITED BY SIZE                                 
                 WS-FLIGHT-STATUS DELIMITED BY SIZE                     
                 INTO ROW6O                                             
              END-STRING                                                
           END-IF                                                       
                                                                        
           IF WS-ROW = 7 THEN                                           
              STRING WS-FLIGHT-NUM DELIMITED BY SIZE                    
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-ORIGIN DELIMITED BY SIZE                     
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-DESTINATION DELIMITED BY SIZE                
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-DEPARTURE-T(1:16) DELIMITED BY SIZE          
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-ARRIVAL-T(1:16) DELIMITED BY SIZE            
                 '  ' DELIMITED BY SIZE                                 
                 WS-FLIGHT-STATUS DELIMITED BY SIZE                     
                 INTO ROW7O                                             
              END-STRING                                                
           END-IF                                                       
                                                                        
           IF WS-ROW = 8 THEN                                           
              STRING WS-FLIGHT-NUM DELIMITED BY SIZE                    
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-ORIGIN DELIMITED BY SIZE                     
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-DESTINATION DELIMITED BY SIZE                
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-DEPARTURE-T(1:16) DELIMITED BY SIZE          
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-ARRIVAL-T(1:16) DELIMITED BY SIZE            
                 '  ' DELIMITED BY SIZE                                 
                 WS-FLIGHT-STATUS DELIMITED BY SIZE                     
                 INTO ROW8O                                             
              END-STRING                                                
           END-IF                                                       
                                                                        
           IF WS-ROW = 9 THEN                                           
              STRING WS-FLIGHT-NUM DELIMITED BY SIZE                    
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-ORIGIN DELIMITED BY SIZE                     
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-DESTINATION DELIMITED BY SIZE                
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-DEPARTURE-T(1:16) DELIMITED BY SIZE          
                 ' ' DELIMITED BY SIZE                                  
                 WS-FLIGHT-ARRIVAL-T(1:16) DELIMITED BY SIZE            
                 '  ' DELIMITED BY SIZE                                 
                 WS-FLIGHT-STATUS DELIMITED BY SIZE                     
                 INTO ROW9O                                             
              END-STRING                                                
           END-IF.                                                      
       SEND-SCREEN-PARA.                                                
           MOVE WS-MSG TO MSGO                                          
                                                                        
           EXEC CICS SEND                                               
              MAPSET(WS-CICS-MAPSET)                                    
              MAP('SCRN1')                                              
              FROM(SCRN1O)                                              
              DATAONLY                                                  
              FREEKB                                                    
           END-EXEC                                                     
           EXEC CICS RETURN                                             
              TRANSID(WS-CICS-TRANSID)                                  
              COMMAREA(WS-COMMAREA)                                     
              LENGTH(66)                                                
           END-EXEC.                                                    
       CLEAN-ROWS-PARA.                                                 
           MOVE SPACES TO ROW1O                                         
           MOVE SPACES TO ROW2O                                         
           MOVE SPACES TO ROW3O                                         
           MOVE SPACES TO ROW4O                                         
           MOVE SPACES TO ROW5O                                         
           MOVE SPACES TO ROW6O                                         
           MOVE SPACES TO ROW7O                                         
           MOVE SPACES TO ROW8O                                         
           MOVE SPACES TO ROW9O.                                        
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