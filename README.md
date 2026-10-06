# Pokémon Stadium Lite

Proyecto desarrollado para el laboratorio de repaso de la materia **Desarrollo de Software III** (Universidad del Valle, Sede Tuluá).

## Descripción del Diseño
La aplicación sigue una arquitectura por capas (client, model, logic, ui) basada en la Programación Orientada a Objetos (POO):
* **`co.edu.univalle.model.Pokemon`**: Clase modelo que encapsula los atributos (HP, Atk, Def, Speed, Tipo, Sprite) y métodos de estado del Pokémon.
* **`co.edu.univalle.client.PokeApiClient`**: Encargado de realizar las peticiones HTTP asíncronas a [PokeAPI](https://pokeapi.co/) mediante `java.net.http.HttpClient` y parsear los datos requeridos utilizando `org.json`.
* **`co.edu.univalle.logic.Battle` y `BattleListener`**: Módulo que gestiona la lógica de combate por turnos en un hilo secundario y notifica los eventos a la UI mediante una interfaz listener personalizada (`onTurn`, `onHpChanged`, `onBattleEnded`).
* **`co.edu.univalle.ui.MainFrame`**: Interfaz gráfica en Swing que implementa `SwingWorker` para evitar bloqueos del hilo principal de la interfaz (`EDT`) durante peticiones de red y renderizado de imágenes.

## Reglas y Fórmula de Daño
1. **Orden de Turnos:** Determinado por el atributo `Speed` (con desempate aleatorio).
2. **Fórmula de Daño Base:**
   
    * **Golpe Crítico:** 10% de probabilidad ($x1.5$).
    * **Efectividad:** Triángulo elemental para tipos primarios ($x1.3$ para Agua > Fuego > Planta > Agua, $x0.7$ para la inversa).
    * **Límite de Vida:** El HP no puede ser menor a 0.

### ¿Porque esta y no otra fórmula?
El usar la fórmula: $$daño = (ATK \cdot rand(0,1) - DEF \cdot rand(0,1)) \cdot crítico \cdot efectividad$$
Usaba dos números aleatorios independiente para ataque y defensa, lo cual genera resultados muy dispares porque un ATK con un random muy bajo y una defensa con un random muy alto dan un golpe muy suave.
Un defensor con DEF alta casi siempre se volvia inmortal alargando el combate demasiado.

## Instrucciones de Ejecución
1. Abrir el proyecto en **IntelliJ IDEA**.
2. Asegurarse de tener configurado **JDK 11** o superior.
3. Cargar las dependencias Maven haciendo clic en **Reload All Maven Projects** sobre el archivo `pom.xml`.
4. Ejecutar la clase principal: `co.edu.univalle.ui.MainFrame`.