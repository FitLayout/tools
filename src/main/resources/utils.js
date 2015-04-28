
function dumpLogicalSubtree(root, prefix)
{
	println(prefix + '- ' + root);
	for (var i = 0; i < root.getChildCount(); i++)
		dumpLogicalSubtree(root.getChildArea(i), "  " + prefix);
}

function dumpLogicalTree()
{
	dumpLogicalSubtree(proc.getLogicalAreaTree().getRoot(), "");
}
