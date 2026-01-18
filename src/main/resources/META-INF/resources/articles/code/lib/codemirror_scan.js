// ---------------
// GRASS support
// ---------------

// Zakladní konfigurace (+addony) - mode si doplní každý plugin sám při
// zvýrazňování
var codeMirrorConfig = {
	styleActiveLine : true,
	lineNumbers : true,
	lineWrapping : false,
	readOnly : true,
	matchTags : {
		bothTags : true
	},
	extraKeys: {"Ctrl-J": "toMatchingTag"},
	matchBrackets : true
}

/*
 * Není to zrovna hezké řešení, ale je to jediné "slušné" jak obejít fakt, že
 * není možné zjistit, kdy Vaadin nahrál pomocí svého JS všechen obsah a je
 * na něj možné aplikovat můj JS - časovač běží dokud nenarazí na první element,
 * který je <pre> a má třídu codemirror_<něco> ... to by mělo být dostatnečně
 * adresné pro určení elementu code
 */
var timer;
var codemirrorScan = function() {	
	var elements = $("[codemirror]");
	if (elements.length > 0) {
		for (var i = 0; i < elements.length; i++) {			
			var mimetype = elements[i].getAttribute("mimetype");			
			codeMirrorConfig.mode = mimetype;				
			CodeMirror.fromTextArea(elements[i], codeMirrorConfig);
		}
		clearInterval(timer);
	}
};
timer = setInterval(codemirrorScan, 100);
