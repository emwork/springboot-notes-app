function submitForm() {
    var delta = editor.getContents();
    var deltaJson = JSON.stringify(delta);
    var text = editor.getText();
    images1 = $("#editor img")
    setExplicitDim(images1);
    var justHtml = editor.root.innerHTML;
    // Copy HTML content in hidden form
    $('#inputItemText').val( text.replaceAll('\n', ' ') );
    $('#inputItemHtml').val( justHtml );

    // prepare htmlSnippet
    editor.deleteText(100,editor.getLength())
    var htmlSnippet = editor.root.innerHTML;
    images1 = $("#editor img")
    scaleImagesDefault(images1);
    $('#htmlSnippet').val(editor.root.innerHTML);

    // Post form
    $("#form1").submit();
    //return false;
}

function scaleImages(images1, minHeight) {
		for (i=0; i<images1.length; i++) {
			images1[i].removeAttribute("class");
			ratio = images1[i].naturalWidth/images1[i].naturalHeight;
			images1[i].setAttribute('height', minHeight);
			images1[i].setAttribute('width', Math.round(minHeight*ratio));
			console.log(images1[i]);
		}
}
function scaleImagesDefault(images1) {
	scaleImages(images1, 100);
}

function setExplicitDim(images1) {
	for (i=0; i<images1.length; i++) {
		images1[i].setAttribute('width', images1[i].naturalWidth);
		images1[i].setAttribute('height', images1[i].naturalHeight);
	}
}
