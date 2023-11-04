const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<dom-module id="grass-tabs" theme-for="vaadin-tabs">
  <template>
    <style>
      
      :host {
        min-height: 1.6rem;
        margin: 0;
        padding: 0;      
      }     
      
      :host(:not([orientation="vertical"])) {
        min-height: 1.6rem;
        margin: 0;
        padding: 0;
      }
      
      :host([orientation="horizontal"]) [part="tabs"] {
        min-height: 0;
        margin: 0;
        padding: 0;      
      }

    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
