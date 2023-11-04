const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<dom-module id="grass-text-field" theme-for="vaadin-text-field">
  <template>
    <style>

	  :host {
        padding: 0;
      }

      [part="value"],
      [part="input-field"] ::slotted(input) {
        background-color: #fff;
      }
      
      [part="input-field"], [part="input-field"]:hover {      	
    	background-color: #fff;    	
    	border: 1px solid #ddd;    	
    	font-weight: normal;
    	color: #555;
	  }
	  
	  :host(:hover:not([readonly]):not([focused])) [part="input-field"]::after {
        opacity: 0.05;
      }
	  
    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);