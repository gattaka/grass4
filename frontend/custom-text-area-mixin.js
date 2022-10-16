const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<dom-module id="grass-text-area" theme-for="vaadin-text-area ">
  <template>
    <style>
    
      :host {
		padding: 0;
      }

      [part="input-field"], [part="input-field"] ::slotted(textarea) {
        background-color: #fff;
        border: 1px solid #ddd;
        font-weight: normal;
    	color: #555;    	
      }
      
      :host(:hover:not([readonly]):not([focused])) [part="input-field"] {
		background-color: #fafafa;
      }
      
    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);