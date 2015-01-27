println('Segmentation started');

proc.initAreaTree('FitLayout.Grouping');
proc.apply('FitLayout.Segm.FindLines', {useConsistentStyle: false, maxLineEmSpace: 1.5});
proc.apply('FitLayout.Segm.HomogeneousLeaves');
proc.apply('FitLayout.Segm.SuperAreas', {depthLimit: 2});
