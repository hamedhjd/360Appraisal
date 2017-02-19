INSERT INTO mdl_course_modules (course,module,instance,visible,visibleold,idnumber,groupmode,groupingid,completion,completiongradeitemnumber,completionview,completionexpected,availability,showdescription,added) VALUES('829','16','0','1','1','','0','0','1',NULL,'0','0',NULL,'0','1473060237')

INSERT INTO mdl_quiz (name,timeopen,timeclose,timelimit,overduehandling,graceperiod,grade,attempts,grademethod,questionsperpage,navmethod,shuffleanswers,preferredbehaviour,canredoquestions,attemptonlast,showuserpicture,decimalpoints,questiondecimalpoints,showblocks,subnet,delay1,delay2,browsersecurity,completionpass,completionattemptsexhausted,course,intro,introformat,timemodified,password,reviewattempt,reviewcorrectness,reviewmarks,reviewspecificfeedback,reviewgeneralfeedback,reviewrightanswer,reviewoverallfeedback) VALUES('TestQuiz','0','0','0','autoabandon','0',10,'0','1','1','free','1','deferredfeedback','0','0','0','2','-1','0','','0','0','-','0','0','829','','1','1473060237','','69904','4368','4368','4368','4368','4368','4368')

INSERT INTO mdl_quiz_sections (quizid,firstslot,heading,shufflequestions) VALUES('450','1','','0')

UPDATE mdl_course_modules SET instance = '450' WHERE id = '1026'

INSERT INTO mdl_context (contextlevel,instanceid,depth,path) VALUES('70','1026','0',NULL)

UPDATE mdl_context SET contextlevel = '70',instanceid = '1026',depth = '6',path = '/1/608/5813/5846/5847/5857' WHERE id='5857'

INSERT INTO mdl_quiz_feedback (quizid,feedbacktext,feedbacktextformat,mingrade,maxgrade) VALUES('450','','1','0',11)

UPDATE mdl_quiz_feedback SET feedbacktext = '' WHERE id = '487'

INSERT INTO mdl_grade_items (courseid,categoryid,itemname,itemtype,itemmodule,iteminstance,itemnumber,iteminfo,idnumber,calculation,gradetype,grademax,grademin,scaleid,outcomeid,gradepass,multfactor,plusfactor,aggregationcoef,aggregationcoef2,sortorder,display,decimals,locked,locktime,needsupdate,weightoverride,timecreated,timemodified,hidden) VALUES('829','423','TestQuiz','mod','quiz','450','0',NULL,'',NULL,'1',10,'0',NULL,NULL,'0',1,'0','0','0','6','0',NULL,'0','0','1','0','1473060237','1473060237','0')

INSERT INTO mdl_grade_items_history (courseid,categoryid,itemname,itemtype,itemmodule,iteminstance,itemnumber,iteminfo,idnumber,calculation,gradetype,grademax,grademin,scaleid,outcomeid,gradepass,multfactor,plusfactor,aggregationcoef,aggregationcoef2,sortorder,display,decimals,locked,locktime,needsupdate,weightoverride,timemodified,hidden,action,oldid,source,loggeduser) VALUES('829','423','TestQuiz','mod','quiz','450','0',NULL,'',NULL,'1','10.00000','0.00000',NULL,NULL,'0.00000','1.00000','0.00000','0.00000','0.00000','6','0',NULL,'0','0','1','0','1473060237','0','1','899',NULL,'2')

UPDATE mdl_grade_items SET needsupdate = '1' WHERE (itemtype='course' OR id='899') AND courseid='829'

UPDATE mdl_course_modules SET instance = '450' WHERE id = '1026'

UPDATE mdl_quiz SET intro = '' WHERE id = '450'

UPDATE mdl_course_sections SET sequence = '1024,1025,1026' WHERE id = '1516'

UPDATE mdl_course_modules SET section = '1516' WHERE id = '1026'

INSERT INTO mdl_grade_items_history (courseid,categoryid,itemname,itemtype,itemmodule,iteminstance,itemnumber,iteminfo,idnumber,calculation,gradetype,grademax,grademin,scaleid,outcomeid,gradepass,multfactor,plusfactor,aggregationcoef,aggregationcoef2,sortorder,display,decimals,locked,locktime,needsupdate,weightoverride,timemodified,hidden,action,oldid,source,loggeduser) VALUES('829','423','TestQuiz','mod','quiz','450','0',NULL,'',NULL,'1',10,0,NULL,NULL,'0.00000',1,0,0,0,'6','0',NULL,'0','0','1','0','1473060237','0','2','899',NULL,'2')

UPDATE mdl_grade_items SET needsupdate = '0' WHERE id = '899'

UPDATE mdl_grade_items SET needsupdate = '0' WHERE id = '897'

