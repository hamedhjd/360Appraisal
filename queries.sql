/*
INSERT INTO `mdl_quiz` (`id`, `course`, `name`, `intro`, `introformat`,
`timeopen`, `timeclose`, `timelimit`, `overduehandling`, `graceperiod`, `preferredbehaviour`,
`canredoquestions`, `attempts`, `attemptonlast`, `grademethod`, `decimalpoints`, `questiondecimalpoints`,
`reviewattempt`, `reviewcorrectness`, `reviewmarks`, `reviewspecificfeedback`, `reviewgeneralfeedback`,
`reviewrightanswer`, `reviewoverallfeedback`, `questionsperpage`, `navmethod`, `shuffleanswers`,
`sumgrades`, `grade`, `timecreated`, `timemodified`, `password`, `subnet`,
`browsersecurity`, `delay1`, `delay2`, `showuserpicture`, `showblocks`, `completionattemptsexhausted`, `completionpass`) VALUES
(1, 828, 'مسابقه تستی', '', 1, 1424156400, 1424286000, 1200, 'autoabandon', 0, 'deferredfeedback',
0, 1, 0, 1, 2, -1, 65536, 0, 0, 0, 0, 0, 0, 6, 'free', 1, '12.00000', '12.00000', 0, 1424279366, '', '', '-', 0, 0, 0, 0, 0, 0)*/

/*
create table cal_user_role
(
    userId varchar(255),
    roleCode varchar(255),
    primary key(userId)
);
*/

/*
create table cal_kpi_info
(
    kpiCode varchar(255),
    kpiValue varchar(255),
    primary key(kpiCode)
);
*/

/*
create table cal_asseeRCode_assorRCode_factor
(
    assesseeRCode varchar(255),
    assessorRCode varchar(255),
    factor varchar(255),
    primary key(assesseeRCode, assessorRCode)
);
*/

/*
create table cal_quesCode_asseeRCode_factor
(
    questionCode varchar(255),
    assesseeRCode varchar(255),
    factor varchar(255),
    primary key(questionCode, assesseeRCode)
);
*/

/*
create table cal_asseeRCode_kpiCode_factor
(
    assesseeRCode varchar(255),
    kpiCode varchar(255),
    factor varchar(255),
    primary key(assesseeRCode, kpiCode)
);
*/

/*
create table cal_quesCode_kpiCode_factor
(
    quesCode varchar(255),
    kpiCode varchar(255),
    factor varchar(255),
    primary key(quesCode, kpiCode)
);
*/

/*
create table cal_userRCode_managerRCode
(
    userRCode varchar(255),
    managerRCode varchar(255),
    primary key(userRCode, managerRCode)
);
*/