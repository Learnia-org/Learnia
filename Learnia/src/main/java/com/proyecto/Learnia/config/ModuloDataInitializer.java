package com.proyecto.Learnia.config;

import com.proyecto.Learnia.entity.Categoria;
import com.proyecto.Learnia.entity.Modulo;
import com.proyecto.Learnia.entity.ModuloPregunta;
import com.proyecto.Learnia.repository.CategoriaRepository;
import com.proyecto.Learnia.repository.ModuloPreguntaRepository;
import com.proyecto.Learnia.repository.ModuloRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ModuloDataInitializer implements CommandLineRunner {

    private final ModuloRepository moduloRepository;
    private final ModuloPreguntaRepository moduloPreguntaRepository;
    private final CategoriaRepository categoriaRepository;

    public ModuloDataInitializer(ModuloRepository moduloRepository,
                                  ModuloPreguntaRepository moduloPreguntaRepository,
                                  CategoriaRepository categoriaRepository) {
        this.moduloRepository = moduloRepository;
        this.moduloPreguntaRepository = moduloPreguntaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public void run(String... args) {
        Optional<Categoria> matematica = categoriaRepository.findById(1L);
        Optional<Categoria> fisica = categoriaRepository.findById(2L);
        Optional<Categoria> informatica = categoriaRepository.findById(3L);
        Optional<Categoria> ingles = categoriaRepository.findById(4L);

        matematica.ifPresent(this::sembrarMatematica);
        fisica.ifPresent(this::sembrarFisica);
        informatica.ifPresent(this::sembrarInformatica);
        ingles.ifPresent(this::sembrarIngles);
    }

    private void crearModuloSiNoExiste(Categoria categoria, int numero, String titulo, String descripcion,
                                        String icono, List<Object[]> preguntasData) {
        boolean existe = moduloRepository.findByCategoria_IdCategoriaAndNumero(categoria.getIdCategoria(), numero).isPresent();
        if (existe) {
            return;
        }

        Modulo modulo = new Modulo();
        modulo.setCategoria(categoria);
        modulo.setNumero(numero);
        modulo.setTitulo(titulo);
        modulo.setDescripcion(descripcion);
        modulo.setIcono(icono);
        modulo = moduloRepository.save(modulo);

        List<ModuloPregunta> preguntas = new ArrayList<>();
        for (Object[] fila : preguntasData) {
            ModuloPregunta pregunta = new ModuloPregunta();
            pregunta.setModulo(modulo);
            pregunta.setEnunciado((String) fila[0]);
            pregunta.setOpcionA((String) fila[1]);
            pregunta.setOpcionB((String) fila[2]);
            pregunta.setOpcionC((String) fila[3]);
            pregunta.setOpcionD((String) fila[4]);
            pregunta.setRespuestaCorrecta((String) fila[5]);
            pregunta.setExplicacion((String) fila[6]);
            preguntas.add(pregunta);
        }
        moduloPreguntaRepository.saveAll(preguntas);
    }

    private void sembrarMatematica(Categoria categoria) {
        crearModuloSiNoExiste(categoria, 1, "Aritmética Básica",
                "Operaciones fundamentales: suma, resta, multiplicación y división.",
                "fa-solid fa-plus-minus", List.of(
                        new Object[]{"¿Cuál es el resultado de 8 + 15?", "21", "23", "22", "24", "B",
                                "8 + 15 = 23."},
                        new Object[]{"¿Cuál es el resultado de 9 x 7?", "56", "63", "72", "54", "B",
                                "9 x 7 = 63."},
                        new Object[]{"¿Cuál es el resultado de 144 ÷ 12?", "10", "11", "12", "13", "C",
                                "144 ÷ 12 = 12."},
                        new Object[]{"¿Cuál es el resultado de 50 - 18?", "32", "30", "28", "34", "A",
                                "50 - 18 = 32."},
                        new Object[]{"¿Cuál es el orden correcto de operaciones (PEMDAS)?", "Suma, resta, multiplicación, división", "Paréntesis, exponentes, multiplicación/división, suma/resta", "División, multiplicación, suma, resta", "Exponentes, paréntesis, resta, suma", "B",
                                "PEMDAS: Paréntesis, Exponentes, Multiplicación/División, Suma/Resta."}
                ));

        crearModuloSiNoExiste(categoria, 2, "Álgebra",
                "Expresiones algebraicas, variables y operaciones con polinomios.",
                "fa-solid fa-square-root-variable", List.of(
                        new Object[]{"Si x = 4, ¿cuál es el valor de 3x + 2?", "12", "14", "16", "10", "B",
                                "3(4) + 2 = 12 + 2 = 14."},
                        new Object[]{"¿Cuál es el resultado de simplificar 2a + 3a?", "5a", "6a", "5a²", "a", "A",
                                "2a + 3a = 5a, se suman los coeficientes de términos semejantes."},
                        new Object[]{"¿Cuál es el resultado de (x + 2)(x + 3)?", "x² + 5x + 6", "x² + 6x + 5", "x² + 5x + 5", "x² + 6", "A",
                                "Usando la propiedad distributiva: x² + 3x + 2x + 6 = x² + 5x + 6."},
                        new Object[]{"¿Qué es una variable en álgebra?", "Un número fijo", "Un símbolo que representa un valor desconocido", "Una operación matemática", "Un signo de igualdad", "B",
                                "Una variable representa un valor que puede cambiar o que es desconocido."},
                        new Object[]{"¿Cuál es el término independiente en 5x² + 3x - 7?", "5", "3", "-7", "x²", "C",
                                "El término independiente es el que no tiene variable: -7."}
                ));

        crearModuloSiNoExiste(categoria, 3, "Geometría",
                "Figuras planas, áreas, perímetros y propiedades básicas.",
                "fa-solid fa-shapes", List.of(
                        new Object[]{"¿Cuántos lados tiene un hexágono?", "5", "6", "7", "8", "B",
                                "Un hexágono tiene 6 lados."},
                        new Object[]{"¿Cuál es la fórmula del área de un rectángulo?", "lado x lado", "base x altura", "base + altura", "2 x (base + altura)", "B",
                                "El área de un rectángulo es base por altura."},
                        new Object[]{"¿Cuánto suman los ángulos internos de un triángulo?", "90°", "180°", "270°", "360°", "B",
                                "La suma de los ángulos internos de cualquier triángulo es 180°."},
                        new Object[]{"¿Cuál es la fórmula del área de un círculo?", "2πr", "πr²", "πd", "r²", "B",
                                "El área de un círculo es pi por el radio al cuadrado."},
                        new Object[]{"¿Cómo se llama un triángulo con sus tres lados iguales?", "Escaleno", "Isósceles", "Equilátero", "Rectángulo", "C",
                                "Un triángulo equilátero tiene sus tres lados y ángulos iguales."}
                ));

        crearModuloSiNoExiste(categoria, 4, "Fracciones y Decimales",
                "Conversión y operaciones entre fracciones y números decimales.",
                "fa-solid fa-percent", List.of(
                        new Object[]{"¿Cuál es el equivalente decimal de 1/4?", "0.25", "0.4", "0.5", "0.75", "A",
                                "1 dividido entre 4 es igual a 0.25."},
                        new Object[]{"¿Cuál es el resultado de 1/2 + 1/4?", "1/6", "2/6", "3/4", "1/4", "C",
                                "1/2 equivale a 2/4, sumado a 1/4 da 3/4."},
                        new Object[]{"¿Cuál fracción es mayor: 3/5 o 2/3?", "3/5", "2/3", "Son iguales", "No se puede saber", "B",
                                "2/3 ≈ 0.667 es mayor que 3/5 = 0.6."},
                        new Object[]{"¿Cómo se convierte 0.6 a fracción?", "6/10", "6/100", "60/1", "1/6", "A",
                                "0.6 equivale a 6/10, que se simplifica a 3/5."},
                        new Object[]{"¿Cuál es el resultado de multiplicar 2/3 x 3/4?", "1/2", "6/12", "5/7", "2/4", "A",
                                "2/3 x 3/4 = 6/12, que simplificado es 1/2."}
                ));

        crearModuloSiNoExiste(categoria, 5, "Ecuaciones Lineales",
                "Resolución de ecuaciones de primer grado con una incógnita.",
                "fa-solid fa-equals", List.of(
                        new Object[]{"Resuelve: x + 5 = 12", "x = 5", "x = 7", "x = 8", "x = 17", "B",
                                "x = 12 - 5 = 7."},
                        new Object[]{"Resuelve: 2x = 18", "x = 7", "x = 8", "x = 9", "x = 10", "C",
                                "x = 18 ÷ 2 = 9."},
                        new Object[]{"Resuelve: 3x - 4 = 11", "x = 3", "x = 4", "x = 5", "x = 6", "C",
                                "3x = 15, entonces x = 5."},
                        new Object[]{"¿Qué representa la solución de una ecuación lineal en una gráfica?", "Un área", "Un punto en una recta numérica", "Una curva", "Un volumen", "B",
                                "La solución de una ecuación con una incógnita es un punto específico."},
                        new Object[]{"Resuelve: x/2 + 3 = 7", "x = 8", "x = 6", "x = 4", "x = 10", "A",
                                "x/2 = 4, entonces x = 8."}
                ));

        crearModuloSiNoExiste(categoria, 6, "Estadística Básica",
                "Media, mediana, moda y análisis de datos simples.",
                "fa-solid fa-chart-column", List.of(
                        new Object[]{"¿Cuál es la media de 2, 4, 6, 8?", "4", "5", "6", "10", "B",
                                "(2+4+6+8)/4 = 20/4 = 5."},
                        new Object[]{"¿Qué es la moda en un conjunto de datos?", "El valor promedio", "El valor que más se repite", "El valor central", "La diferencia entre el mayor y menor", "B",
                                "La moda es el dato que aparece con mayor frecuencia."},
                        new Object[]{"¿Cuál es la mediana de 3, 5, 7, 9, 11?", "5", "7", "9", "6", "B",
                                "El valor central al ordenar los datos es 7."},
                        new Object[]{"¿Qué mide el rango en estadística?", "El promedio de los datos", "La diferencia entre el valor máximo y mínimo", "El valor más frecuente", "La cantidad de datos", "B",
                                "El rango es la diferencia entre el dato mayor y el menor."},
                        new Object[]{"Si lanzas una moneda, ¿cuál es la probabilidad de obtener cara?", "1/4", "1/2", "1/3", "1", "B",
                                "Hay 2 resultados posibles igualmente probables, por lo tanto 1/2."}
                ));
    }

    private void sembrarFisica(Categoria categoria) {
        crearModuloSiNoExiste(categoria, 1, "Introducción a la Física y Unidades",
                "Magnitudes físicas, unidades del Sistema Internacional y notación científica.",
                "fa-solid fa-ruler-combined", List.of(
                        new Object[]{"¿Cuál es la unidad de medida de la masa en el SI?", "Gramo", "Kilogramo", "Libra", "Newton", "B",
                                "El kilogramo (kg) es la unidad base de masa en el Sistema Internacional."},
                        new Object[]{"¿Cuál es la unidad de medida de la fuerza?", "Joule", "Watt", "Newton", "Pascal", "C",
                                "La fuerza se mide en Newtons (N)."},
                        new Object[]{"¿Qué estudia la física?", "Solo los seres vivos", "La materia, la energía y sus interacciones", "Únicamente las plantas", "Las reacciones químicas exclusivamente", "B",
                                "La física estudia la materia, la energía, y cómo interactúan entre sí."},
                        new Object[]{"¿Cuál es la unidad de tiempo en el SI?", "Minuto", "Hora", "Segundo", "Día", "C",
                                "El segundo (s) es la unidad base de tiempo."},
                        new Object[]{"¿Qué es una magnitud vectorial?", "Solo tiene magnitud", "Tiene magnitud y dirección", "Es siempre positiva", "No se puede medir", "B",
                                "Una magnitud vectorial tiene magnitud, dirección y sentido, como la velocidad."}
                ));

        crearModuloSiNoExiste(categoria, 2, "Cinemática",
                "Movimiento, velocidad, aceleración y trayectorias.",
                "fa-solid fa-gauge-high", List.of(
                        new Object[]{"¿Cuál es la fórmula de la velocidad?", "v = d x t", "v = d / t", "v = t / d", "v = d + t", "B",
                                "La velocidad es igual a la distancia dividida entre el tiempo."},
                        new Object[]{"¿Qué mide la aceleración?", "El cambio de posición", "El cambio de velocidad en el tiempo", "La distancia recorrida", "La masa de un objeto", "B",
                                "La aceleración es el cambio de velocidad por unidad de tiempo."},
                        new Object[]{"En un MRU (movimiento rectilíneo uniforme), la velocidad es...", "Variable", "Constante", "Cero siempre", "Infinita", "B",
                                "En el MRU la velocidad se mantiene constante."},
                        new Object[]{"¿Cuál es la unidad de la velocidad en el SI?", "km/h", "m/s", "m/s²", "N", "B",
                                "La unidad de velocidad en el SI es metros por segundo (m/s)."},
                        new Object[]{"Si un auto acelera uniformemente desde el reposo, su gráfico velocidad-tiempo es...", "Una curva", "Una línea horizontal", "Una línea recta ascendente", "Un punto", "C",
                                "En un movimiento uniformemente acelerado, la velocidad aumenta linealmente con el tiempo."}
                ));

        crearModuloSiNoExiste(categoria, 3, "Dinámica y Leyes de Newton",
                "Fuerzas, inercia y las tres leyes de Newton.",
                "fa-solid fa-weight-hanging", List.of(
                        new Object[]{"¿Qué establece la primera ley de Newton?", "F = m x a", "Un cuerpo en reposo o movimiento uniforme permanece así salvo que actúe una fuerza", "Toda acción tiene una reacción", "La energía se conserva", "B",
                                "La ley de inercia establece que un cuerpo mantiene su estado de reposo o movimiento salvo fuerza externa."},
                        new Object[]{"¿Cuál es la fórmula de la segunda ley de Newton?", "F = m / a", "F = m + a", "F = m x a", "F = a / m", "C",
                                "La segunda ley de Newton establece que Fuerza = masa x aceleración."},
                        new Object[]{"¿Qué dice la tercera ley de Newton?", "Todo cuerpo tiende al reposo", "A toda acción corresponde una reacción igual y opuesta", "La fuerza es proporcional a la masa", "La energía no se crea ni se destruye", "B",
                                "La ley de acción y reacción indica que las fuerzas se dan en pares opuestos."},
                        new Object[]{"¿Qué es la inercia?", "La capacidad de un cuerpo de acelerar", "La resistencia de un cuerpo a cambiar su estado de movimiento", "La fuerza de gravedad", "La energía cinética", "B",
                                "La inercia es la resistencia que opone un cuerpo a cambiar su estado de reposo o movimiento."},
                        new Object[]{"¿Cuál es la unidad de fuerza que resulta de F = m x a?", "kg", "m/s", "Newton (kg·m/s²)", "Joule", "C",
                                "El Newton se define como kg·m/s², resultado de multiplicar masa por aceleración."}
                ));

        crearModuloSiNoExiste(categoria, 4, "Energía y Trabajo",
                "Energía cinética, potencial, trabajo mecánico y conservación de la energía.",
                "fa-solid fa-bolt", List.of(
                        new Object[]{"¿Cuál es la fórmula del trabajo mecánico?", "W = F x d", "W = m x a", "W = F / d", "W = m x g", "A",
                                "El trabajo es igual a la fuerza aplicada por la distancia recorrida."},
                        new Object[]{"¿Qué es la energía cinética?", "La energía por posición", "La energía asociada al movimiento", "La energía química", "La energía térmica", "B",
                                "La energía cinética es la energía que posee un cuerpo debido a su movimiento."},
                        new Object[]{"¿Qué establece la ley de conservación de la energía?", "La energía se pierde con el tiempo", "La energía no se crea ni se destruye, solo se transforma", "La energía siempre aumenta", "La energía es constante solo en líquidos", "B",
                                "La energía total de un sistema aislado permanece constante, solo cambia de forma."},
                        new Object[]{"¿Cuál es la unidad de energía en el SI?", "Newton", "Watt", "Joule", "Pascal", "C",
                                "La energía se mide en Joules (J)."},
                        new Object[]{"¿Qué es la energía potencial gravitatoria?", "La energía debido a la velocidad", "La energía almacenada por la posición de un objeto respecto a un nivel de referencia", "La energía eléctrica", "La energía del sonido", "B",
                                "La energía potencial gravitatoria depende de la altura y la masa del objeto."}
                ));

        crearModuloSiNoExiste(categoria, 5, "Electricidad Básica",
                "Carga eléctrica, corriente, voltaje y resistencia.",
                "fa-solid fa-bolt-lightning", List.of(
                        new Object[]{"¿Cuál es la unidad de la corriente eléctrica?", "Voltio", "Watt", "Amperio", "Ohmio", "C",
                                "La corriente eléctrica se mide en Amperios (A)."},
                        new Object[]{"¿Qué establece la Ley de Ohm?", "V = I / R", "V = I x R", "I = V x R", "R = V x I", "B",
                                "La Ley de Ohm establece que Voltaje = Corriente x Resistencia."},
                        new Object[]{"¿Cuál es la unidad de la resistencia eléctrica?", "Voltio", "Amperio", "Ohmio", "Watt", "C",
                                "La resistencia eléctrica se mide en Ohmios (Ω)."},
                        new Object[]{"¿Qué es un circuito eléctrico?", "Un tipo de imán", "Un camino cerrado por donde circula la corriente", "Una fuente de luz", "Un aislante", "B",
                                "Un circuito es la trayectoria cerrada que permite el flujo de corriente eléctrica."},
                        new Object[]{"¿Qué es un material conductor?", "Un material que impide el paso de la corriente", "Un material que permite el paso de la corriente fácilmente", "Un material magnético", "Un material que no conduce calor", "B",
                                "Los conductores, como los metales, permiten el flujo fácil de electrones."}
                ));

        crearModuloSiNoExiste(categoria, 6, "Ondas y Sonido",
                "Propiedades de las ondas, frecuencia, longitud de onda y el sonido.",
                "fa-solid fa-wave-square", List.of(
                        new Object[]{"¿Qué es la frecuencia de una onda?", "La distancia entre dos crestas", "El número de ciclos por segundo", "La altura de la onda", "La velocidad de la onda", "B",
                                "La frecuencia es el número de oscilaciones completas por segundo, medida en Hertz."},
                        new Object[]{"¿Cuál es la unidad de la frecuencia?", "Metro", "Segundo", "Hertz", "Newton", "C",
                                "La frecuencia se mide en Hertz (Hz)."},
                        new Object[]{"¿El sonido necesita un medio material para propagarse?", "No, se propaga en el vacío", "Sí, necesita un medio como el aire o el agua", "Solo se propaga en sólidos", "Solo se propaga en el espacio", "B",
                                "El sonido es una onda mecánica y requiere un medio material para propagarse."},
                        new Object[]{"¿Qué es la longitud de onda?", "El tiempo de un ciclo completo", "La distancia entre dos puntos equivalentes consecutivos de la onda", "La velocidad de propagación", "La energía de la onda", "B",
                                "La longitud de onda es la distancia entre dos crestas o valles consecutivos."},
                        new Object[]{"¿Qué tipo de onda es la luz?", "Onda mecánica", "Onda electromagnética", "Onda sonora", "Onda sísmica", "B",
                                "La luz es una onda electromagnética y no requiere medio material para propagarse."}
                ));
    }

    private void sembrarInformatica(Categoria categoria) {
        crearModuloSiNoExiste(categoria, 1, "Fundamentos de Computación",
                "Hardware, software y componentes básicos de una computadora.",
                "fa-solid fa-desktop", List.of(
                        new Object[]{"¿Qué es el hardware?", "Los programas de la computadora", "Los componentes físicos de la computadora", "El sistema operativo", "Un tipo de virus", "B",
                                "El hardware son los componentes físicos y tangibles de un equipo."},
                        new Object[]{"¿Qué es el software?", "Los componentes físicos", "El conjunto de programas e instrucciones", "El monitor y el teclado", "La fuente de poder", "B",
                                "El software es el conjunto de programas y aplicaciones que ejecuta la computadora."},
                        new Object[]{"¿Cuál de los siguientes es un dispositivo de entrada?", "Monitor", "Impresora", "Teclado", "Bocinas", "C",
                                "El teclado permite ingresar datos, por eso es un dispositivo de entrada."},
                        new Object[]{"¿Qué componente se considera el 'cerebro' de la computadora?", "RAM", "CPU", "Disco duro", "Fuente de poder", "B",
                                "La CPU (Unidad Central de Procesamiento) ejecuta las instrucciones y procesa los datos."},
                        new Object[]{"¿Qué es la memoria RAM?", "Almacenamiento permanente", "Memoria temporal de trabajo", "Un tipo de software", "Un dispositivo de salida", "B",
                                "La RAM almacena datos temporalmente mientras el equipo está encendido."}
                ));

        crearModuloSiNoExiste(categoria, 2, "Lógica de Programación",
                "Algoritmos, variables, condicionales y estructuras de control.",
                "fa-solid fa-code", List.of(
                        new Object[]{"¿Qué es un algoritmo?", "Un lenguaje de programación", "Una secuencia ordenada de pasos para resolver un problema", "Un tipo de dato", "Un error de programación", "B",
                                "Un algoritmo es una serie finita de pasos para resolver un problema o tarea."},
                        new Object[]{"¿Qué estructura se usa para repetir un bloque de código?", "Condicional", "Bucle o ciclo", "Variable", "Función vacía", "B",
                                "Los bucles (for, while) permiten repetir instrucciones varias veces."},
                        new Object[]{"¿Qué hace una estructura condicional (if)?", "Repite un bloque de código", "Evalúa una condición y decide qué camino seguir", "Declara variables", "Elimina errores automáticamente", "B",
                                "Un 'if' evalúa una condición booleana y ejecuta un bloque según el resultado."},
                        new Object[]{"¿Qué es una variable en programación?", "Un valor fijo que nunca cambia", "Un espacio de memoria para almacenar datos que pueden cambiar", "Un tipo de función", "Un mensaje de error", "B",
                                "Una variable almacena datos que pueden modificarse durante la ejecución del programa."},
                        new Object[]{"¿Cuál es el resultado lógico de: 5 > 3 && 2 > 4?", "true", "false", "5", "error", "B",
                                "5 > 3 es true, pero 2 > 4 es false, y con && ambas deben ser true, por lo tanto el resultado es false."}
                ));

        crearModuloSiNoExiste(categoria, 3, "Estructuras de Datos Básicas",
                "Arreglos, listas, pilas y colas.",
                "fa-solid fa-layer-group", List.of(
                        new Object[]{"¿Qué es un arreglo (array)?", "Una variable simple", "Una estructura que almacena múltiples valores del mismo tipo", "Un tipo de función", "Un error de sintaxis", "B",
                                "Un arreglo permite guardar varios elementos del mismo tipo en una sola estructura."},
                        new Object[]{"¿Qué política de acceso usa una Pila (Stack)?", "FIFO (primero en entrar, primero en salir)", "LIFO (último en entrar, primero en salir)", "Acceso aleatorio", "Ordenado alfabéticamente", "B",
                                "Una pila sigue el principio LIFO: el último elemento en entrar es el primero en salir."},
                        new Object[]{"¿Qué política de acceso usa una Cola (Queue)?", "LIFO", "FIFO (primero en entrar, primero en salir)", "Acceso aleatorio", "No tiene orden definido", "B",
                                "Una cola sigue el principio FIFO: el primer elemento en entrar es el primero en salir."},
                        new Object[]{"¿Cuál es el índice del primer elemento en la mayoría de los arreglos de programación?", "1", "0", "-1", "Depende del color", "B",
                                "En la mayoría de los lenguajes de programación, los arreglos inician en el índice 0."},
                        new Object[]{"¿Qué es una lista enlazada?", "Un arreglo de tamaño fijo", "Una estructura de nodos conectados mediante referencias", "Un tipo de base de datos", "Un algoritmo de ordenamiento", "B",
                                "Una lista enlazada está compuesta por nodos que apuntan al siguiente elemento."}
                ));

        crearModuloSiNoExiste(categoria, 4, "Bases de Datos",
                "Tablas, relaciones, claves primarias y consultas básicas.",
                "fa-solid fa-database", List.of(
                        new Object[]{"¿Qué es una base de datos?", "Un programa antivirus", "Una colección organizada de datos", "Un navegador web", "Un tipo de red social", "B",
                                "Una base de datos es un conjunto organizado de información que se puede consultar y gestionar."},
                        new Object[]{"¿Qué es una clave primaria (Primary Key)?", "Un dato que se puede repetir", "Un identificador único de cada registro en una tabla", "El nombre de la tabla", "Una consulta SQL", "B",
                                "La clave primaria identifica de forma única a cada registro de una tabla."},
                        new Object[]{"¿Qué significa SQL?", "Structured Query Language", "System Query Logic", "Simple Question Language", "Standard Query List", "A",
                                "SQL significa Structured Query Language (Lenguaje de Consulta Estructurado)."},
                        new Object[]{"¿Qué comando SQL se usa para obtener datos de una tabla?", "INSERT", "SELECT", "DELETE", "UPDATE", "B",
                                "El comando SELECT se utiliza para consultar y obtener datos de una tabla."},
                        new Object[]{"¿Qué es una clave foránea (Foreign Key)?", "Una clave que identifica de forma única una fila", "Una columna que referencia la clave primaria de otra tabla", "Un tipo de índice", "Un error de base de datos", "B",
                                "La clave foránea establece una relación entre dos tablas, apuntando a la clave primaria de otra."}
                ));

        crearModuloSiNoExiste(categoria, 5, "Redes e Internet",
                "Conceptos básicos de redes, IP, y funcionamiento de internet.",
                "fa-solid fa-network-wired", List.of(
                        new Object[]{"¿Qué significa IP en el contexto de redes?", "Internet Provider", "Internet Protocol", "Internal Process", "Input Port", "B",
                                "IP significa Internet Protocol, que identifica dispositivos en una red."},
                        new Object[]{"¿Qué es una red LAN?", "Una red de área amplia mundial", "Una red de área local", "Un tipo de virus", "Un navegador web", "B",
                                "LAN (Local Area Network) es una red que conecta dispositivos en un área geográfica pequeña."},
                        new Object[]{"¿Qué protocolo se usa para navegar páginas web de forma segura?", "FTP", "HTTP", "HTTPS", "SMTP", "C",
                                "HTTPS es la versión segura y cifrada del protocolo HTTP."},
                        new Object[]{"¿Qué es un router?", "Un dispositivo que almacena archivos", "Un dispositivo que dirige el tráfico de datos entre redes", "Un tipo de cable", "Un programa antivirus", "B",
                                "El router dirige los paquetes de datos entre diferentes redes."},
                        new Object[]{"¿Qué es el DNS?", "Un tipo de virus informático", "Un sistema que traduce nombres de dominio a direcciones IP", "Un lenguaje de programación", "Un dispositivo físico de red", "B",
                                "El DNS (Domain Name System) traduce nombres como 'google.com' a direcciones IP."}
                ));

        crearModuloSiNoExiste(categoria, 6, "Seguridad Informática Básica",
                "Contraseñas seguras, virus, phishing y buenas prácticas digitales.",
                "fa-solid fa-shield-halved", List.of(
                        new Object[]{"¿Qué es el phishing?", "Un tipo de hardware", "Una técnica de engaño para robar información personal", "Un lenguaje de programación", "Un tipo de red", "B",
                                "El phishing busca engañar a las personas para que entreguen información confidencial."},
                        new Object[]{"¿Cuál de las siguientes es una buena práctica de seguridad?", "Usar la misma contraseña en todos los sitios", "Usar contraseñas largas y únicas para cada cuenta", "Compartir contraseñas por correo", "Desactivar el antivirus", "B",
                                "Usar contraseñas únicas y robustas reduce el riesgo de accesos no autorizados."},
                        new Object[]{"¿Qué es un malware?", "Un programa útil de oficina", "Software malicioso diseñado para dañar o infiltrarse en un sistema", "Un tipo de hardware", "Un protocolo de red", "B",
                                "El malware es cualquier software diseñado para causar daño o acceso no autorizado."},
                        new Object[]{"¿Qué función cumple un antivirus?", "Acelerar la computadora", "Detectar y eliminar software malicioso", "Aumentar la memoria RAM", "Conectar a internet", "B",
                                "El antivirus protege el sistema detectando y eliminando amenazas."},
                        new Object[]{"¿Qué es la autenticación de dos factores (2FA)?", "Usar dos contraseñas iguales", "Un método de seguridad que requiere dos formas de verificación", "Un tipo de virus", "Un protocolo de red antiguo", "B",
                                "La 2FA agrega una capa extra de seguridad al requerir dos métodos de verificación distintos."}
                ));
    }

    private void sembrarIngles(Categoria categoria) {
        crearModuloSiNoExiste(categoria, 1, "Vocabulario Básico",
                "Palabras y frases esenciales para el día a día.",
                "fa-solid fa-book", List.of(
                        new Object[]{"¿Cómo se dice 'casa' en inglés?", "Car", "House", "Horse", "Hand", "B",
                                "'House' significa 'casa' en inglés."},
                        new Object[]{"¿Cómo se dice 'gracias' en inglés?", "Please", "Sorry", "Thank you", "Excuse me", "C",
                                "'Thank you' es la forma de decir 'gracias' en inglés."},
                        new Object[]{"¿Qué significa la palabra 'water'?", "Fuego", "Agua", "Aire", "Tierra", "B",
                                "'Water' significa 'agua' en español."},
                        new Object[]{"¿Cómo se dice 'libro' en inglés?", "Book", "Look", "Cook", "Hook", "A",
                                "'Book' es la palabra en inglés para 'libro'."},
                        new Object[]{"¿Qué significa 'friend'?", "Enemigo", "Familia", "Amigo", "Profesor", "C",
                                "'Friend' significa 'amigo' en español."}
                ));

        crearModuloSiNoExiste(categoria, 2, "Verb To Be",
                "Uso del verbo 'to be' (am, is, are) en presente.",
                "fa-solid fa-circle-check", List.of(
                        new Object[]{"Completa: I ___ a student.", "is", "am", "are", "be", "B",
                                "Con el pronombre 'I' se usa 'am': I am a student."},
                        new Object[]{"Completa: She ___ happy.", "am", "are", "is", "be", "C",
                                "Con 'she' (tercera persona singular) se usa 'is'."},
                        new Object[]{"Completa: They ___ from Guatemala.", "is", "am", "are", "be", "C",
                                "Con 'they' (plural) se usa 'are'."},
                        new Object[]{"¿Cuál es la forma negativa correcta de 'You are ready'?", "You isn't ready", "You aren't ready", "You amn't ready", "You not ready", "B",
                                "La forma negativa de 'are' es 'aren't': You aren't ready."},
                        new Object[]{"¿Cuál es la forma correcta en pregunta: '___ you tired?'", "Is", "Are", "Am", "Be", "B",
                                "Con 'you' se usa 'are' para formar la pregunta: Are you tired?"}
                ));

        crearModuloSiNoExiste(categoria, 3, "Presente Simple",
                "Estructura y uso del Simple Present para hábitos y rutinas.",
                "fa-solid fa-clock", List.of(
                        new Object[]{"Completa: He ___ to school every day.", "go", "goes", "going", "gone", "B",
                                "Con la tercera persona singular (he/she/it) se agrega 's': He goes."},
                        new Object[]{"¿Cuál es la forma negativa correcta de 'They play soccer'?", "They doesn't play soccer", "They don't play soccer", "They not play soccer", "They isn't play soccer", "B",
                                "La forma negativa en presente simple con 'they' es 'don't': They don't play soccer."},
                        new Object[]{"Completa la pregunta: '___ she like coffee?'", "Do", "Does", "Is", "Are", "B",
                                "Con la tercera persona singular se usa 'does' para preguntas: Does she like coffee?"},
                        new Object[]{"¿Cuál oración usa correctamente el presente simple?", "I working every day", "I works every day", "I work every day", "I am work every day", "C",
                                "Con 'I' el verbo va en su forma base: I work every day."},
                        new Object[]{"Completa: The sun ___ in the east.", "rise", "rises", "rising", "rose", "B",
                                "Con 'the sun' (tercera persona singular) se agrega 's': rises."}
                ));

        crearModuloSiNoExiste(categoria, 4, "Pasado Simple",
                "Verbos regulares e irregulares en Simple Past.",
                "fa-solid fa-clock-rotate-left", List.of(
                        new Object[]{"¿Cuál es el pasado del verbo 'go'?", "Goed", "Went", "Gone", "Going", "B",
                                "'Go' es un verbo irregular y su pasado es 'went'."},
                        new Object[]{"¿Cuál es el pasado del verbo regular 'play'?", "Played", "Plaied", "Plays", "Playing", "A",
                                "Los verbos regulares en pasado agregan 'ed': play -> played."},
                        new Object[]{"Completa: Yesterday, I ___ a movie.", "watch", "watched", "watching", "watches", "B",
                                "Se usa el pasado simple 'watched' porque la acción ocurrió ayer."},
                        new Object[]{"¿Cuál es la forma negativa correcta de 'She ate breakfast'?", "She not ate breakfast", "She didn't ate breakfast", "She didn't eat breakfast", "She doesn't ate breakfast", "C",
                                "La forma negativa del pasado usa 'didn't' + verbo en infinitivo: didn't eat."},
                        new Object[]{"¿Cuál es el pasado del verbo 'have'?", "Haved", "Has", "Had", "Having", "C",
                                "'Have' es un verbo irregular y su pasado es 'had'."}
                ));

        crearModuloSiNoExiste(categoria, 5, "Preposiciones y Artículos",
                "Uso de preposiciones de lugar/tiempo y artículos (a, an, the).",
                "fa-solid fa-location-dot", List.of(
                        new Object[]{"Completa: The book is ___ the table.", "in", "on", "at", "under", "B",
                                "Se usa 'on' cuando algo está sobre una superficie: on the table."},
                        new Object[]{"Completa: I was born ___ 2005.", "in", "on", "at", "by", "A",
                                "Se usa 'in' para años: in 2005."},
                        new Object[]{"Completa: She arrives ___ 6 PM.", "in", "on", "at", "for", "C",
                                "Se usa 'at' para horas específicas: at 6 PM."},
                        new Object[]{"¿Cuál artículo se usa antes de 'apple'?", "A", "An", "The only", "No article", "B",
                                "Se usa 'an' antes de palabras que inician con sonido vocálico: an apple."},
                        new Object[]{"Completa: There is ___ university near my house.", "a", "an", "the only", "no article needed", "A",
                                "'University' comienza con sonido de consonante (Y), por lo que se usa 'a'."}
                ));

        crearModuloSiNoExiste(categoria, 6, "Vocabulario de Uso Cotidiano",
                "Palabras y expresiones comunes para conversaciones diarias.",
                "fa-solid fa-comments", List.of(
                        new Object[]{"¿Cómo se dice 'buenos días' en inglés?", "Good night", "Good morning", "Good afternoon", "Good evening", "B",
                                "'Good morning' significa 'buenos días' en inglés."},
                        new Object[]{"¿Qué significa 'How are you?'", "¿Cómo te llamas?", "¿Cómo estás?", "¿Dónde vives?", "¿Qué haces?", "B",
                                "'How are you?' significa '¿Cómo estás?'."},
                        new Object[]{"¿Cómo se dice 'por favor' en inglés?", "Sorry", "Please", "Thanks", "Excuse me", "B",
                                "'Please' es la forma de decir 'por favor' en inglés."},
                        new Object[]{"¿Qué significa 'What time is it?'", "¿Qué día es hoy?", "¿Qué hora es?", "¿Cuál es tu nombre?", "¿De dónde eres?", "B",
                                "'What time is it?' significa '¿Qué hora es?'."},
                        new Object[]{"¿Cómo se dice 'me gustaría' en inglés?", "I like", "I liked", "I would like", "I am liking", "C",
                                "'I would like' es una forma cortés de decir 'me gustaría' en inglés."}
                ));
    }
}
