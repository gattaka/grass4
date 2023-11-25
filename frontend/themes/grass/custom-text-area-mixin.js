const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<dom-module id="grass-text-area" theme-for="vaadin-text-area">
  <template>
    <style>
      
    </style>
  </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);