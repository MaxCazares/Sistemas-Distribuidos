const barraBusqueda = document.querySelector("#search");
const divBusqueda = document.querySelector('#divBusqueda');
const divCarrito = document.querySelector('#divCarrito');
const carritoCompras = document.querySelector('#carritoCompras');

let foto = null;

// Estas funciones son de prueba
//  ++++++++++++++++++++++++++++++++++++++++++++++++++++++
const elemento = {
    id_articulo: 2,
    nombre: 'Camara Canon',
    descripcion: 'Esta nueva',
    precio: 1200,
    cantidad: 3,
    imagen: '../img/20.jpg',
}

const agregarElemento = () => {
    // const { id_articulo, nombre, descripcion, precio, imagen } = elemento;
    // crearElementoBusqueda(id_articulo, nombre, descripcion, precio, imagen);

    const {id_articulo, nombre, descripcion, precio, cantidad, imagen} = elemento;
    crearElementoCarrito(id_articulo, nombre, descripcion, precio, cantidad, imagen);
}
// +++++++++++++++++++++++++++++++++++++++++++++++++++++++

const buscarArticulo = () => {
    const busqueda = barraBusqueda.value;
    let cliente = new WSClient("/Servicio/rest/ws");
    cliente.post("buscarArticulo", { descripcion: busqueda },
        (code, result) => {
            if (code === 200) {
                for (let i = 0; i < result.length; i++) {
                    foto = result[i].foto;
                    crearElementoBusqueda(
                        result[i].id_articulo,
                        result[i].nombre,
                        result[i].descripcion,
                        result[i].precio,
                        "data:image/jpeg;base64," + foto,
                    );
                }
            } else {
                alert(JSON.stringify(result));
            }
        });
}

const comprarArticulo = (id_articulo, cantidad) => {
    let cliente = new WSClient("/Servicio/rest/ws");
    cliente.post("comprarArticulo", {
        articulo: {
            id_articulo,
            cantidad: cantidad.value,
        }
    },
        (code, result) => {
            code === 200 ? alert('Articulo agregado al carrito') : alert(JSON.stringify(result))
        });
    // alert(`id: ${id_articulo} cantidad: ${cantidad.value}`);
}

const consultarCarrito = () => {
    let cliente = new WSClient("/Servicio/rest/ws");
    cliente.post('consultarCarrito', {},
        (code, result) => {
            if (code === 200) {
                for (let i = 0; i < result.length; i++) {
                    foto = result[i].foto;
                    crearElementoCarrito(
                        result[i].id_articulo,
                        result[i].nombre,
                        result[i].descripcion,
                        result[i].precio,
                        result[i].cantidad,
                        "data:image/jpeg;base64," + foto,
                    );
                }
            } else {
                alert(JSON.stringify(result))
            }
        }
    );
}

const eliminarArticulo = (id_articulo, cantidad) => {
    let cliente = new WSClient("/Servicio/rest/ws");
    cliente.post('eliminarArticulo', {
        articulo: {
            id_articulo,
            cantidad,
        }
    },
        (code, result) => {
            if (code === 200) {
                alert('Articulo eliminado del carrito');
                eliminarArticuloCarrito(id_articulo);
            } else {
                alert('Error en la captura: ' + JSON.stringify(result));
            }
        });
}

const eliminarArticuloCarrito = (id_articulo) => {
    const articulo = document.querySelector(`#elementoCarrito${id_articulo}`);
    articulo.parentNode.removeChild(articulo);
}

const verCarrito = () => {
    divBusqueda.classList.add('hidden');
    divCarrito.classList.remove('hidden');
    consultarCarrito();
}

const ocultarCarrito = () => {
    divCarrito.classList.add('hidden');
    divBusqueda.classList.remove('hidden');
}

const crearElementoBusqueda = (id_articulo, nombre, descripcion, precio, imagen) => {
    const ElementoBusqueda = document.createElement('div');
    ElementoBusqueda.classList.add('flex');
    ElementoBusqueda.classList.add('flex-wrap');
    ElementoBusqueda.setAttribute('id', `ID${id_articulo}`);

    const image = document.createElement('img');
    image.classList.add('lg:w-80');
    image.classList.add('w-full');
    image.classList.add('object-cover');
    image.classList.add('object-center');
    image.classList.add('rounded');
    image.setAttribute('src', imagen);

    const divInfo = document.createElement('div');
    divInfo.classList.add('lg:w-1/2');
    divInfo.classList.add('w-full');
    divInfo.classList.add('lg:pl-10');
    divInfo.classList.add('lg:py-6');
    divInfo.classList.add('mt-6');
    divInfo.classList.add('lg:mt-0');

    const nombreProducto = document.createElement('h1');
    nombreProducto.classList.add('text-3xl');
    nombreProducto.classList.add('title-font');
    nombreProducto.classList.add('font-medium');
    nombreProducto.classList.add('mb-3');
    nombreProducto.textContent = nombre;

    const descripcionProducto = document.createElement('p');
    descripcionProducto.classList.add('leading-relaxed');
    descripcionProducto.classList.add('text-justify');
    descripcionProducto.textContent = descripcion;

    const divPrecio = document.createElement('div');
    divPrecio.classList.add('flex');
    divPrecio.classList.add('mt-6');
    divPrecio.classList.add('border-b-2');
    divPrecio.classList.add('border-gray-600');
    divPrecio.classList.add('mb-5');
    divPrecio.classList.add('py-4');

    const divPrecio2 = document.createElement('div');
    divPrecio2.classList.add('flex');
    divPrecio2.classList.add('flex-row');
    divPrecio2.classList.add('space-x-36');

    const precioProducto = document.createElement('p');
    precioProducto.classList.add('font-medium');
    precioProducto.classList.add('mt-1');
    precioProducto.classList.add('text-2xl');
    precioProducto.textContent = "$" + precio;

    const spanCantidad = document.createElement('span');
    spanCantidad.classList.add('space-x-2');

    const labelCantidad = document.createElement('label');
    labelCantidad.classList.add('text-md');
    labelCantidad.textContent = 'Cantidad';

    const inputCantidad = document.createElement('input');
    inputCantidad.classList.add('text-md');
    inputCantidad.classList.add('w-20');
    inputCantidad.classList.add('rounded');
    inputCantidad.classList.add('border');
    inputCantidad.classList.add('border-gray-70');
    inputCantidad.classList.add('focus:ring-2');
    inputCantidad.classList.add('focus:ring-indigo-900');
    inputCantidad.classList.add('bg-transparent');
    inputCantidad.classList.add('appearance-none');
    inputCantidad.classList.add('py-2');
    inputCantidad.classList.add('focus:outline-none');
    inputCantidad.classList.add('focus:border-indigo-500');
    inputCantidad.classList.add('pr-2');
    inputCantidad.setAttribute('type', 'number');
    inputCantidad.setAttribute('min', '1');
    inputCantidad.setAttribute('step', '1');
    inputCantidad.setAttribute('value', '1');
    inputCantidad.setAttribute('id', `cantidadCompra${id_articulo}`);

    const botonComprar = document.createElement('button');
    botonComprar.classList.add('flex');
    botonComprar.classList.add('font-semibold');
    botonComprar.classList.add('py-2');
    botonComprar.classList.add('px-6');
    botonComprar.classList.add('rounded-lg');
    botonComprar.classList.add('bg-gradient-to-r');
    botonComprar.classList.add('from-pink-500');
    botonComprar.classList.add('to-yellow-500');
    botonComprar.classList.add('hover:bg-gradient-to-r');
    botonComprar.classList.add('hover:from-green-400');
    botonComprar.classList.add('hover:to-blue-500');
    // botonComprar.setAttribute('onclick', `comprarArticulo(${id_articulo}, "document.querySelector('cantidadCompra${id_articulo}')")`);
    botonComprar.setAttribute('onclick', "comprarArticulo('" + id_articulo + "', document.querySelector('#cantidadCompra" + id_articulo + "'))");
    botonComprar.setAttribute('id', 'compraBoton' + id_articulo);
    botonComprar.textContent = 'Comprar';

    spanCantidad.appendChild(labelCantidad);
    spanCantidad.appendChild(inputCantidad);

    divPrecio2.appendChild(precioProducto);
    divPrecio2.appendChild(spanCantidad);

    divPrecio.appendChild(divPrecio2);

    divInfo.appendChild(nombreProducto);
    divInfo.appendChild(descripcionProducto);
    divInfo.appendChild(divPrecio);
    divInfo.appendChild(botonComprar);

    ElementoBusqueda.appendChild(image);
    ElementoBusqueda.appendChild(divInfo);

    divBusqueda.appendChild(ElementoBusqueda);
}

const crearElementoCarrito = (id_articulo, nombre, descripcion, precio, cantidad, imagen) => {
    const elementoCarrito = document.createElement('li');
    elementoCarrito.classList.add('flex');
    elementoCarrito.classList.add('flex-col');
    elementoCarrito.classList.add('w-full');
    elementoCarrito.classList.add('space-x-4');
    elementoCarrito.classList.add('py-6');
    elementoCarrito.setAttribute('id', `elementoCarrito${id_articulo}`);

    const divGeneral = document.createElement('div');
    divGeneral.classList.add('flex');
    divGeneral.classList.add('w-full');
    divGeneral.classList.add('space-x-4');

    const imagenProducto = document.createElement('img');
    imagenProducto.classList.add('flex');
    imagenProducto.classList.add('object-cover');
    imagenProducto.classList.add('w-32');
    imagenProducto.classList.add('h-32');
    imagenProducto.setAttribute('src', imagen);

    const divSecundario = document.createElement('div');
    divSecundario.classList.add('flex');
    divSecundario.classList.add('flex-col');
    divSecundario.classList.add('font-semibold');
    divSecundario.classList.add('text-right');
    divSecundario.classList.add('w-full');
    divSecundario.classList.add('pb-4');
    divSecundario.classList.add('space-y-2');

    const nombreProducto = document.createElement('p');
    nombreProducto.classList.add('text-lg');
    nombreProducto.classList.add('text-left');
    nombreProducto.textContent = nombre;

    const descripcionProducto = document.createElement('p');
    descripcionProducto.classList.add('leading-relaxed');
    descripcionProducto.classList.add('font-normal');
    descripcionProducto.classList.add('text-justify');
    descripcionProducto.textContent = descripcion;

    const precioProducto = document.createElement('p');
    precioProducto.textContent = `Precio: $${precio}`;

    const cantidadProducto = document.createElement('p');
    cantidadProducto.textContent = `Cantidad: ${cantidad}`;

    const costoProducto = document.createElement('p');
    costoProducto.textContent = `Costo total: $${precio * cantidad}`;

    const botonEliminar = document.createElement('button');
    botonEliminar.classList.add('font-semibold');
    botonEliminar.classList.add('text-center');
    botonEliminar.classList.add('py-2');
    botonEliminar.classList.add('rounded-lg');
    botonEliminar.classList.add('bg-gradient-to-r');
    botonEliminar.classList.add('from-pink-500');
    botonEliminar.classList.add('to-yellow-500');
    botonEliminar.classList.add('hover:bg-gradient-to-r');
    botonEliminar.classList.add('hover:from-green-400');
    botonEliminar.classList.add('hover:to-blue-500');
    botonEliminar.setAttribute('onclick', "eliminarArticulo('" + id_articulo + "','" + cantidad + "' )");
    botonEliminar.textContent = 'Eliminar';

    divSecundario.appendChild(nombreProducto);
    divSecundario.appendChild(descripcionProducto);
    divSecundario.appendChild(precioProducto);
    divSecundario.appendChild(cantidadProducto);
    divSecundario.appendChild(costoProducto);
    divSecundario.appendChild(botonEliminar);

    divGeneral.appendChild(imagenProducto);
    divGeneral.appendChild(divSecundario);

    elementoCarrito.appendChild(divGeneral);
    carritoCompras.appendChild(elementoCarrito);
}

class WSClient {
    constructor(url) {
        this.url = url;
        this.post = function (operation, args, callback) {
            var request = new XMLHttpRequest();
            var body = "";
            var pairs = [];
            var name;
            try {
                for (name in args) {
                    var value = args[name];
                    if (typeof (value) !== "string")
                        value = JSON.stringify(value);
                    pairs.push(name + '=' + encodeURI(value).replace(/=/g, '%3D').replace(/&/g, '%26').replace(/%20/g, '+'));
                }
                body = pairs.join('&');
                request.open('POST', url + '/' + operation);
                request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                request.setRequestHeader('Content-Length', body.length);
                request.responseType = 'json';
                request.onload = function () {
                    if (callback != null)
                        callback(request.status, resolveReferences(request.response));
                };
                request.send(body);
            }
            catch (e) {
                alert("Error: " + e.message);
            }
        };
    }
}

const resolveReferences = json => {
    if (typeof json === 'string') json = JSON.parse(json);

    var byid = {}, // all objects by id
        refs = []; // references to objects that could not be resolved

    json = (function recurse(obj, prop, parent) {
        if (typeof obj !== 'object' || !obj) // a primitive value
            return obj;
        if (Object.prototype.toString.call(obj) === '[object Array]') {
            for (var i = 0; i < obj.length; i++)
                // check also if the array element is not a primitive value
                if (typeof obj[i] !== 'object' || !obj[i]) // a primitive value
                    continue;
                else if ("$ref" in obj[i])
                    obj[i] = recurse(obj[i], i, obj);
                else
                    obj[i] = recurse(obj[i], prop, obj);
            return obj;
        }
        if ("$ref" in obj) {
            // a reference
            var ref = obj.$ref;
            if (ref in byid) return byid[ref];
            // else we have to make it lazy:
            refs.push([parent, prop, ref]);
            return;
        }
        else if ("$id" in obj) {
            var id = obj.$id;
            delete obj.$id;
            if ("$values" in obj) // an array
                obj = obj.$values.map(recurse);
            else // a plain object
                for (var prop in obj)
                    obj[prop] = recurse(obj[prop], prop, obj);
            byid[id] = obj;
        }
        return obj;
    })(json); // run it!

    for (var i = 0; i < refs.length; i++) {
        // resolve previously unknown references
        var ref = refs[i];
        ref[0][ref[1]] = byid[ref[2]];
        // Notice that this throws if you put in a reference at top-level
    }
    return json;
}