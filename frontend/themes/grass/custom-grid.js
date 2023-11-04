const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<dom-module id="grass-grid" theme-for="vaadin-grid">
  <template>
    <style>
      
		[part~="cell"] ::slotted(vaadin-grid-cell-content) img {
			margin-bottom: -2px;
			width: 20px;
		}
	
		:host([theme~='compact']) [part~='first-row'] [part~='cell']:not([part~='details-cell']) {
            min-height: 32px;
        }
	
		:host([theme~="compact"]) [part~="cell"] {
		    min-height: var(--lumo-size-m);
		}
      
    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
