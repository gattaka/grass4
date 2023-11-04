const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<dom-module id="grass-tab" theme-for="vaadin-tab">
  <template>
    <style>
      
      :host {
        margin: 0 0.2rem 0 0;
        padding: 0 0.3rem 0.5rem 0.3rem;  
        min-height: 0;     
      }     

    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);
