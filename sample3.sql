INSERT INTO mdl_course_modules (course,module,instance,visible,visibleold,idnumber,groupmode,groupingid,completion,completiongradeitemnumber,completionview,completionexpected,availability,showdescription,added) VALUES('828','16','0','1','1','','0','0','1',NULL,'0','0',NULL,'0','1472742010')

INSERT INTO mdl_quiz (name,timeopen,timeclose,timelimit,overduehandling,graceperiod,grade,attempts,grademethod,questionsperpage,navmethod,shuffleanswers,preferredbehaviour,canredoquestions,attemptonlast,showuserpicture,decimalpoints,questiondecimalpoints,showblocks,subnet,delay1,delay2,browsersecurity,completionpass,completionattemptsexhausted,course,intro,introformat,timemodified,password,reviewattempt,reviewcorrectness,reviewmarks,reviewspecificfeedback,reviewgeneralfeedback,reviewrightanswer,reviewoverallfeedback) VALUES('Sample3','0','0','0','autoabandon','0',10,'0','1','1','free','1','deferredfeedback','0','0','0','2','-1','0','','0','0','-','0','0','828','','1','1472742010','','69904','4368','4368','4368','4368','4368','4368')

INSERT INTO mdl_quiz_sections (quizid,firstslot,heading,shufflequestions) VALUES('430','1','','0')

UPDATE mdl_course_modules SET instance = '430' WHERE id = '1001'

INSERT INTO mdl_context (contextlevel,instanceid,depth,path) VALUES('70','1001','0',NULL)

UPDATE mdl_context SET contextlevel = '70',instanceid = '1001',depth = '5',path = '/1/608/5813/5814/5830' WHERE id='5830'

INSERT INTO mdl_quiz_feedback (quizid,feedbacktext,feedbacktextformat,mingrade,maxgrade) VALUES('430','','1','0',11)

UPDATE mdl_quiz_feedback SET feedbacktext = '' WHERE id = '471'

INSERT INTO mdl_grade_items (courseid,categoryid,itemname,itemtype,itemmodule,iteminstance,itemnumber,iteminfo,idnumber,calculation,gradetype,grademax,grademin,scaleid,outcomeid,gradepass,multfactor,plusfactor,aggregationcoef,aggregationcoef2,sortorder,display,decimals,locked,locktime,needsupdate,weightoverride,timecreated,timemodified,hidden) VALUES('828','422','Sample3','mod','quiz','430','0',NULL,'',NULL,'1',10,'0',NULL,NULL,'0',1,'0','0','0','5','0',NULL,'0','0','1','0','1472742010','1472742010','0')

INSERT INTO mdl_grade_items_history (courseid,categoryid,itemname,itemtype,itemmodule,iteminstance,itemnumber,iteminfo,idnumber,calculation,gradetype,grademax,grademin,scaleid,outcomeid,gradepass,multfactor,plusfactor,aggregationcoef,aggregationcoef2,sortorder,display,decimals,locked,locktime,needsupdate,weightoverride,timemodified,hidden,action,oldid,source,loggeduser) VALUES('828','422','Sample3','mod','quiz','430','0',NULL,'',NULL,'1','10.00000','0.00000',NULL,NULL,'0.00000','1.00000','0.00000','0.00000','0.00000','5','0',NULL,'0','0','1','0','1472742010','0','1','885',NULL,'2')

UPDATE mdl_grade_items SET needsupdate = '1' WHERE (itemtype='course' OR id='885') AND courseid='828'

UPDATE mdl_course_modules SET instance = '430' WHERE id = '1001'

UPDATE mdl_quiz SET intro = '' WHERE id = '430'

UPDATE mdl_course_sections SET sequence = '995,997,999,1000,1001' WHERE id = '1514'

UPDATE mdl_course_modules SET section = '1514' WHERE id = '1001'

UPDATE mdl_grade_items SET courseid = '828',categoryid = '422',itemname = 'Sample3',itemtype = 'mod',itemmodule = 'quiz',iteminstance = '430',itemnumber = '0',iteminfo = NULL,idnumber = '',calculation = NULL,gradetype = '1',grademax = 10,grademin = 0,scaleid = NULL,outcomeid = NULL,gradepass = '0.00000',multfactor = 1,plusfactor = 0,aggregationcoef = 0,aggregationcoef2 = 0,sortorder = '5',display = '0',decimals = NULL,locked = '0',locktime = '0',needsupdate = '1',weightoverride = '0',timecreated = '1472742010',timemodified = '1472742011',hidden = '0' WHERE id='885'

INSERT INTO mdl_grade_items_history (courseid,categoryid,itemname,itemtype,itemmodule,iteminstance,itemnumber,iteminfo,idnumber,calculation,gradetype,grademax,grademin,scaleid,outcomeid,gradepass,multfactor,plusfactor,aggregationcoef,aggregationcoef2,sortorder,display,decimals,locked,locktime,needsupdate,weightoverride,timemodified,hidden,action,oldid,source,loggeduser) VALUES('828','422','Sample3','mod','quiz','430','0',NULL,'',NULL,'1',10,0,NULL,NULL,'0.00000',1,0,0,0,'5','0',NULL,'0','0','1','0','1472742011','0','2','885',NULL,'2')

UPDATE mdl_grade_items SET needsupdate = '0' WHERE id = '885'

UPDATE mdl_grade_items SET needsupdate = '0' WHERE id = '880'

