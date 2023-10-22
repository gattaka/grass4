import { html } from '@polymer/polymer/lib/utils/html-tag.js';

const $_documentContainer = html`<dom-module id="grass-lumo-button" theme-for="vaadin-button">
  <template>
    <style>
    
      :host {        
        background-color: #fcfcfc;
        border: 1px solid #ddd;
		/* aby bylo tlačítko na stejné úrovni a velikosti jako fieldy */        
        padding-bottom: 2px;    
    	height: 34px;        
    	margin: 0;
      }

	  ::slotted(img) {
        margin-right: 0.50em;
        margin-bottom: -0.25em;        
      }
      
      :host([theme~="menu-button"]) {
        border: none;
        color: #d1d3d6;
        text-shadow: rgba(0,0,0,.9) 0 1px 0;
        text-decoration: none;
        font: 400 14px/1.55 Helvetica,Arial,lucida grande,tahoma,verdana,arial,sans-serif;
        cursor: pointer;
        height: 21px;
      }

      :host([theme~="menu-button"]):hover {
          text-decoration: none;
          color: white;
      }
      
      ::slotted(img.img-only-button) {
        margin-right: 0;
        margin-bottom: -0.25em;        
      }      
      
      [part="prefix"] {
        margin: 0;
      }
      
      [part="label"] {
        margin: 0px 0px -1px 0;
      }
      
      :host(:hover)::before {
        opacity: 0.03;
      }

    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
