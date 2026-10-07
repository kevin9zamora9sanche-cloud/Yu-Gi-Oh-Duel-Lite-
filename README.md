# Yu-Gi-Oh! Duel Lite

Proyecto desarrollado para el **Laboratorio #1** de la materia **Desarrollo de Software III** (Universidad del Valle, Sede Tuluá). Docente: Mg(c). Juan Pablo Pinillos Reina.

Mini-aplicación de escritorio en **Java Swing** que simula un duelo sencillo de Yu-Gi-Oh! entre un jugador y la máquina, usando cartas Monster obtenidas en vivo desde la API [YGOProDeck](https://ygoprodeck.com/api-guide/).

## Integrantes

| Nombres                        | Código  |
|--------------------------------|---------|
| Andrés Felipe Velasquez Moreno | 2459534 |
| Alexander Zamora Sánchez       | 2559731 |

## Descripción del Diseño

La aplicación sigue una arquitectura por capas (`client`, `model`, `logic`, `ui`) basada en la Programación Orientada a Objetos (POO), con responsabilidades separadas:

* **`co.edu.univalle.model.Card`**: Clase modelo que encapsula los atributos de la carta (nombre, ATK, DEF, URL de la imagen).
* **`co.edu.univalle.client.YgoApiClient`**: Realiza las peticiones HTTP a `https://db.ygoprodeck.com/api/v7/randomcard.php` mediante `java.net.http.HttpClient` y parsea el JSON con `org.json`. Valida que la carta obtenida sea de tipo *Monster*; si no lo es, vuelve a solicitar otra.
* **`co.edu.univalle.logic.Duel` y `BattleListener`**: `Duel` contiene las reglas y la lógica del enfrentamiento (turnos, comparación de stats y puntaje). Notifica a la UI mediante la interfaz `BattleListener` (`onTurn`, `onScoreChanged`, `onDuelEnded`), lo que desacopla la lógica de la interfaz.
* **`co.edu.univalle.ui.MainFrame`**: Interfaz gráfica en Swing. Usa `ActionListener` en los botones **Iniciar duelo** y **Elegir carta**, y `SwingWorker` para cargar cartas e imágenes sin bloquear el hilo de la interfaz (`EDT`). Incluye un log de batalla desplazable (`JTextArea` + `JScrollPane`).

### Validaciones y manejo de errores

* El duelo **no inicia** hasta que el jugador y la máquina tengan sus 3 cartas cargadas.
* Los errores se muestran de forma visible al usuario (por ejemplo, "No se pudo cargar la carta" o "Error de red").
* Ninguna petición de red ni carga de imágenes se ejecuta en el EDT.

## Reglas del Duelo

1. **Mazo inicial:** el jugador y la máquina reciben **3 cartas Monster** aleatorias, mostradas con imagen, nombre, ATK y DEF.
2. **Turno inicial:** se define de forma aleatoria.
3. **Cada ronda:**
   * El jugador elige una de sus cartas disponibles.
   * La máquina elige una al azar entre las suyas.
   * Se comparan los stats según la posición de cada carta:

   | Situación | Resultado |
      |---|---|
   | Ambas en ataque | Gana la carta con mayor **ATK** |
   | Una en ataque y otra en defensa | Se compara el **ATK** del atacante contra la **DEF** del defensor |

   <!-- Aclarar aquí cómo se determina la posición (aleatoria, elegida por el jugador, según el turno, etc.) y cómo se resuelven los empates -->
4. **Puntaje:** el ganador de la ronda obtiene **1 punto**.
5. **Fin del duelo:** el primero en ganar **2 de 3 rondas** es el vencedor, y se anuncia en pantalla.
6. **Log de batalla:** registra quién jugó qué carta, el resultado de cada turno y el puntaje acumulado.

## Instrucciones de Ejecución

1. Abrir el proyecto en **IntelliJ IDEA**.
2. Asegurarse de tener configurado **JDK 11** o superior.
3. Cargar las dependencias Maven haciendo clic en **Reload All Maven Projects** sobre el archivo `pom.xml`.
4. Verificar que haya conexión a internet (las cartas se obtienen en vivo desde la API).
5. Ejecutar la clase principal: `co.edu.univalle.ui.MainFrame`.

## Capturas de Pantalla

### Pantalla inicial
![Pantalla inicial](docs/screenshots/inicio.png)

### Cartas cargadas
![Cartas cargadas](docs/screenshots/cartas.png)

### Duelo en curso con log de batalla
![Duelo en curso](docs/screenshots/duelo.png)

### Anuncio del ganador
![Ganador](docs/screenshots/ganador.png)