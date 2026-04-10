window.addEventListener('DOMContentLoaded', () => {
    const fundo = document.getElementById('fundoRetalhos');
    if (!fundo) return;

    const cores = [
        'retalho-cor-1','retalho-cor-2','retalho-cor-3',
        'retalho-cor-4','retalho-cor-5','retalho-cor-6',
        'retalho-cor-7','retalho-cor-8','retalho-cor-9',
        'retalho-cor-10'
    ];

    const patternFactory = [
        (container,row,col) => {
            const isEvenCell = (row+col)%2===0;
            const color1 = cores[(row*2)%cores.length];
            container.innerHTML = `<div class="forma-interna ${color1}" 
               style="width:100%;height:100%;
               clip-path:${isEvenCell ? 'polygon(0 0,100% 0,0 100%)':'polygon(100% 0,100% 100%,0 100%)'}"></div>`;
        },
        (container,row,col) => {
            const color1 = cores[row%cores.length];
            const color2 = cores[(col+3)%cores.length];
            container.innerHTML = `<div class="forma-interna ${color1}" style="width:100%;height:100%;"></div>
            <div class="forma-interna ${color2}" style="width:60%;height:60%;
            clip-path:polygon(50% 0%,100% 50%,50% 100%,0% 50%)"></div>`;
        },
        (container,row,col) => {
            const color1 = cores[(row+col)%cores.length];
            const color2 = cores[(row+col+4)%cores.length];
            container.innerHTML = `<div class="forma-interna ${color1}" style="width:100%;height:100%;"></div>
            <div class="forma-interna ${color2}" style="width:140%;height:40%;
            transform:rotate(${45 + (row%2)*90}deg)"></div>`;
        }
    ];
	
	const cellSize = 80; // mesmo valor do grid-auto-rows
	const rows = Math.ceil(window.innerHeight / cellSize);
	const cols = Math.ceil(window.innerWidth / cellSize);

    for (let row=0; row<rows; row++) {
        for (let col=0; col<cols; col++) {
            const retalho = document.createElement('div');
            retalho.className = 'retalho-miniatura';
            const formaContainer = document.createElement('div');
            formaContainer.className = 'retalho-forma';
            const patternIndex = (row*2+col) % patternFactory.length;
            patternFactory[patternIndex](formaContainer,row,col);
            retalho.appendChild(formaContainer);
            fundo.appendChild(retalho);
        }
    }
});
