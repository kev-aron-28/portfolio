//COMPMAP JOB  (123),'KEVIN',                                           
//             CLASS=A,                                                 
//             MSGCLASS=A,                                              
//             MSGLEVEL=(1,1),                                          
//             PRTY=15,                                                 
//             NOTIFY=&SYSUID                                           
//********************************************************************* 
//* JCL TO COMPILE A  MAP FILE                                          
//********************************************************************  
//        SET  MAPNAME='MPFT03'                                         
//        SET  FILENAME=MPFT03                                          
//********************************************************************* 
//PROCLIB JCLLIB ORDER=(DFH410.CICS.SDFHPROC)                           
//STEP1   EXEC PROC=DFHMAPS,                                            
//             MAPNAME=&MAPNAME,                                        
//             INDEX='DFH410.CICS',                                     
//             MAPLIB='DFH410.CICS.SDFHLOAD',                           
//             DSCTLIB='DFH410.CICS.SDFHMAC'                            
//COPY.SYSUT1 DD DISP=SHR,DSN=PROJECT2.MAPS(&FILENAME)                  
//SYSPRINT DD  SYSOUT=*                                                 
/*                                                                      