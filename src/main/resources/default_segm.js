//default page segmentation process used by the ScriptProcessor when no script name is specified
//page must have been rendered before

//use the default area tree provider for creating the basic area tree
proc.initAreaTree('FitLayout.Grouping');

//apply the default operators
proc.execInternal('default_operators.js');
