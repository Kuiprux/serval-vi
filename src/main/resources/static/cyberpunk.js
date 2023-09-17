let accessResultDiv;
let resultContentDiv;

window.onload = function() {
	init();
	
	applyCutOffs();
	initFancyLines();
}

function init() {
	accessResultDiv = document.getElementById('access-result')
	resultContentDiv = document.getElementById('result-content')
	accessResultDiv.addEventListener('animationend', onShowEnd)
}

function initFancyLines() {
	var lines = document.getElementsByClassName('fancy-line');
	for(let i = 0; i < lines.length; i++) {
		let square = document.createElement('div');
		square.classList.add('fancy-line-box');
		lines[i].appendChild(square);
		let sqWid = getActualWidth(square);
		let lnWid = getActualWidth(lines[i]);
		
		for(let j = 0; j < lnWid + sqWid * 2; j += sqWid) {
			let square = document.createElement('div');
			square.classList.add('fancy-line-box');
			lines[i].appendChild(square);
		}
	}
}

function applyCutOffs() {
	var cutoffs = document.querySelectorAll('[cutoff-corner][cutoff-amount]');
	for (var i = 0; i < cutoffs.length; i++) {
		//add svg path with the value calculated
		var cutoff = cutoffs[i];
		var corner = cutoff.getAttribute('cutoff-corner');
		var amount = cutoff.getAttribute('cutoff-amount');
		var pixAmount = convertToPixels(amount);

		//get the actual position and size of the element
		var rect = cutoff.getBoundingClientRect();
		if (rect.width < pixAmount || rect.height < pixAmount) {
			continue;
		}

		var svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
		var path = document.createElementNS('http://www.w3.org/2000/svg', 'path');

		var dVal = '';
		if (corner == 'top-left')
			dVal += `M0,${pixAmount} ${pixAmount},0 `
		else
			dVal += `M0,0 `
		if (corner == 'top-right')
			dVal += `L${rect.width - pixAmount},0 L${rect.width}, ${pixAmount} `
		else
			dVal += `L${rect.width},0 `

		if (corner == 'bottom-right')
			dVal += `L${rect.width},${rect.height - pixAmount} L${rect.width - pixAmount},${rect.height} `
		else
			dVal += `L${rect.width},${rect.height} `

		if (corner == 'bottom-left')
			dVal += `L${pixAmount},${rect.height} L0,${rect.height - pixAmount} `
		else
			dVal += `L0,${rect.height} `

		dVal += 'Z';

		path.setAttribute('d', dVal);
		//get actually affected style of cutoff
		var cutoffStyle = window.getComputedStyle(cutoff);
		//set border to match with cutoff
		path.style.stroke = cutoffStyle.borderColor;
		path.style.strokeWidth = cutoffStyle.borderWidth;
		path.style.fill = cutoffStyle.backgroundColor;
		//remove cutoff style
		cutoff.style.border = 'none';
		cutoff.style.backgroundColor = 'transparent';
		svg.appendChild(path);
		svg.viewBox = `0 0 ${rect.width} ${rect.height}`;
		svg.style.position = 'absolute';
		svg.style.top = rect.top + 'px';
		svg.style.left = rect.left + 'px';
		svg.style.width = rect.width + 'px';
		svg.style.height = rect.height + 'px';
		svg.style.zIndex = -1;
		svg.style.pointerEvents = 'none';
		document.body.appendChild(svg);
	}
}

function convertToPixels(valueWithUnit) {
	// Create a temporary element with the given value
	const tempElement = document.createElement('div');
	tempElement.style.width = valueWithUnit;

	// Add the element to the document to get computed style
	document.body.appendChild(tempElement);

	// Get the computed pixel value
	const pixelValue = parseFloat(getComputedStyle(tempElement).width);

	// Remove the temporary element from the document
	document.body.removeChild(tempElement);

	return pixelValue;
}

function getActualWidth(element) {
	var style = element.currentStyle || window.getComputedStyle(element),
    width = element.offsetWidth, // or use style.width
    margin = parseFloat(style.marginLeft) + parseFloat(style.marginRight),
    padding = parseFloat(style.paddingLeft) + parseFloat(style.paddingRight),
    border = parseFloat(style.borderLeftWidth) + parseFloat(style.borderRightWidth);
	
	return width + margin - padding + border;
}

function showAccessResult(succeed) {
	if(!succeed) {
		accessResultDiv.classList.add('failed')
		resultContentDiv.innerText = '접속 거부'
	} else {
		accessResultDiv.classList.remove('failed')
		resultContentDiv.innerText = '접속 승인'
	}
	
	accessResultDiv.classList.add('result-appear')
	
}

function onShowEnd(e) {
	if(e.animationName ==	 'appear') {
		accessResultDiv.classList.remove('result-appear')
	}
}