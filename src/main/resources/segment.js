println('Segmentation started');

var srcConfig = {
		url: 'http://www.reuters.com/article/2014/03/28/us-trading-momentum-analysis-idUSBREA2R09M20140328',
		width: 1200,
		height: 800
};
proc.renderPage('FitLayout.CSSBox', srcConfig);

proc.initAreaTree('FitLayout.Grouping');
proc.apply('FitLayout.Segm.FindLines', {useConsistentStyle: false, maxLineEmSpace: 1.5});
proc.apply('FitLayout.Segm.HomogeneousLeaves', {});
proc.apply('FitLayout.Segm.SuperAreas', {depthLimit: 2});

proc.apply('FitLayout.Tools.XMLOutput', {filename: "/tmp/out.xml"});
