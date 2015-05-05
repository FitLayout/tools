println('Init started');

println("Providers:")
println(proc.boxProviderIds);
println(proc.areaProviderIds);
println("Operators:")
println(proc.operatorIds);

//proc.initAreaTree('FitLayout.Grouping');
//proc.apply('FitLayout.Segm.FindLines', {useConsistentStyle: false, maxLineEmSpace: 1.5});

proc.execInternal('utils.js');

println('Init finished');
