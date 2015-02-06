//default operators and their parametres
//used for the initial configuration of the GUIProcessor
//reference from the default_segm.js script as the default operators on the created tree

proc.apply('FitLayout.Segm.FindLines', {useConsistentStyle: false, maxLineEmSpace: 1.5});
proc.apply('FitLayout.Segm.HomogeneousLeaves', {});
proc.apply('FitLayout.Segm.SuperAreas', {depthLimit: 2});

proc.apply('FitLayout.Tag.Entities', {});
proc.apply('FitLayout.Tag.Visual', {trainFile: "train_mix.arff", classIndex: 1});

//proc.apply('FitLayout.Tools.XMLOutput', {filename: "/tmp/out.xml"});
