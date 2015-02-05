//default operators and their parametres
proc.apply('FitLayout.Segm.FindLines', {useConsistentStyle: false, maxLineEmSpace: 1.5});
proc.apply('FitLayout.Segm.HomogeneousLeaves', {});
proc.apply('FitLayout.Segm.SuperAreas', {depthLimit: 2});

//proc.apply('FitLayout.Tools.XMLOutput', {filename: "/tmp/out.xml"});
