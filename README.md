# Pokémon Stadium Lite

Proyecto desarrollado para el laboratorio de repaso de la materia **Desarrollo de Software III** (Universidad del Valle, Sede Tuluá).

## Integrantes
| Nombres | Código |
|---------|---------|
| Andrés Felipe Velasquez Moreno | 2459534 |
| Alexander Zamora Sánchez | 2559731 | 

## Descripción del Diseño
La aplicación sigue una arquitectura por capas (client, model, logic, ui) basada en la Programación Orientada a Objetos (POO):
* **`co.edu.univalle.model.Pokemon`**: Clase modelo que encapsula los atributos (HP, Atk, Def, Speed, Tipo, Sprite) y métodos de estado del Pokémon.
* **`co.edu.univalle.client.PokeApiClient`**: Encargado de realizar las peticiones HTTP asíncronas a [PokeAPI](https://pokeapi.co/) mediante `java.net.http.HttpClient` y parsear los datos requeridos utilizando `org.json`.
* **`co.edu.univalle.logic.Battle` y `BattleListener`**: Módulo que gestiona la lógica de combate por turnos en un hilo secundario y notifica los eventos a la UI mediante una interfaz listener personalizada (`onTurn`, `onHpChanged`, `onBattleEnded`).
* **`co.edu.univalle.ui.MainFrame`**: Interfaz gráfica en Swing que implementa `SwingWorker` para evitar bloqueos del hilo principal de la interfaz (`EDT`) durante peticiones de red y renderizado de imágenes.

## Reglas y Fórmula de Daño

1. **Orden de turnos:** inicia el Pokémon con mayor `Speed`; si empatan, el inicio es aleatorio. Después los turnos se alternan.
2. **Fórmula de daño:**

   $$\text{daño} = \max\left(1,\ \operatorname{round}\left(ATK \cdot \frac{ATK}{ATK + DEF} \cdot v \cdot c \cdot e\right)\right)$$

   Donde:
   * $v$: Es un número random entre 0.85 y 1.0.
   * $c$: **golpe crítico**, vale $1.5$ con 10 % de probabilidad y $1.0$ en el resto.
   * $e$: **efectividad** según el primer tipo del atacante contra el primer tipo del defensor: $1.3$ si Agua > Fuego, Fuego > Planta o Planta > Agua; $0.7$ en la dirección inversa; $1.0$ en cualquier otro caso.
3. **Límite de vida:** el HP nunca baja de 0.
4. **Daño mínimo:** todo golpe hace al menos 1 de daño, para que el combate siempre termine.
5. **Redondeo:** el daño se redondea una sola vez, al final, y ese mismo entero se descuenta, se notifica y se registra en el log.

### ¿Por qué esta fórmula y no otra?

La primera versión era:

$$\text{daño} = (ATK \cdot rand(0,1) - DEF \cdot rand(0,1)) \cdot c \cdot e$$

Tenía dos problemas:

* **Resultados muy dispares:** usaba dos números aleatorios independientes, uno para el ataque y otro para la defensa. Un ATK con un random bajo contra una DEF con un random alto daba un golpe casi nulo, sin relación con los stats reales.
* **Defensores inmortales:** al ser una resta, un defensor con DEF alta dejaba el daño en el mínimo casi siempre, y el combate podía alargarse demasiado.

La fórmula actual usa una **proporción** en lugar de una resta: $\frac{ATK}{ATK + DEF}$ es la fracción del ataque que atraviesa la defensa. Así la defensa reduce el daño sin anularlo, un mayor ATK siempre pega más fuerte, y la aleatoriedad queda limitada a una variación pequeña (85 %–100 %).

| Caso | ATK | DEF | Daño base aprox. |
|---|---|---|---|
| Parejos | 80 | 80 | 40 |
| Atacante fuerte vs. defensor débil | 130 | 50 | 94 |
| Atacante débil vs. defensor muy resistente | 50 | 230 | 9 |

## Instrucciones de Ejecución
1. Abrir el proyecto en **IntelliJ IDEA**.
2. Asegurarse de tener configurado **JDK 11** o superior.
3. Cargar las dependencias Maven haciendo clic en **Reload All Maven Projects** sobre el archivo `pom.xml`.
4. Ejecutar la clase principal: `co.edu.univalle.ui.MainFrame`.