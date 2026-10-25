package es.mariana.dweb.controller;

import es.mariana.dweb.exception.EntrenadorException;
import es.mariana.dweb.model.entities.Entrenador;
import es.mariana.dweb.model.entities.Pokemon;
import es.mariana.dweb.model.service.EntrenadorService;
import es.mariana.dweb.model.service.PokemonService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class EntrenadorController {
    @Autowired
    private EntrenadorService servicio;
    @Autowired
    private PokemonService pokemonService;

    // RESUELTO: listado de entrenadores.
    @GetMapping("/entrenadores")
    public String listado(Model model, HttpSession session) {
        model.addAttribute("entrenadores", servicio.findAll());
        model.addAttribute("entrenadorSeleccionado", session.getAttribute("entrenador"));
        return "entrenadores";
    }


    @PostMapping("/entrenadores/nuevo")
    public String nuevoEntrenador(HttpSession session) {

        return "redirect:/entrenadores/nuevo";
    }




    @PostMapping("/entrenadores/guardar")
    public String guardarEntrenador(
            HttpSession session,
            Model model,
            @RequestParam("nombre") String nombre,
            @RequestParam("tipo") String tipo,
            @RequestParam(value = "id", required = false) Integer id) {

        List<Entrenador> todosEntrenadores = servicio.findAll();
        int nombresRepetidos = 0;
        for (Entrenador entrenador : todosEntrenadores) {
            if (entrenador.getNombre().equalsIgnoreCase(nombre.trim())) {
                nombresRepetidos++;
            }
        }

        if (nombresRepetidos > 0) {
            model.addAttribute("error",
                    "Ya existe un entrenador con ese nombre.");
            Entrenador entrenador = new Entrenador();

            if (id != null) {
                entrenador.setId(id);
            }
            entrenador.setNombre(nombre);
            entrenador.setTipo(tipo);
            model.addAttribute("entrenador", entrenador);
            return "formulario-entrenador";

        } else {
            Entrenador entrenadorGuardado;
            if (id == null) {
                entrenadorGuardado = servicio.crear(nombre, tipo);
            } else {
                entrenadorGuardado = servicio.editar(id, nombre, tipo);
            }
            Entrenador seleccionado =
                    (Entrenador) session.getAttribute("entrenador");

            if (seleccionado != null
                    && seleccionado.getId() == entrenadorGuardado.getId()) {
                session.setAttribute("entrenador", entrenadorGuardado);
            }
        }
        return "redirect:/entrenadores";
    }




    // RESUELTO: selección; se guarda el OBJETO COMPLETO en sesión.
    @PostMapping("/entrenadores/seleccionar")
    public String seleccionar(@RequestParam int id, HttpSession session) {
        session.setAttribute("entrenador", servicio.findById(id));
        return "redirect:/entrenadores";
    }

    // RESUELTO: deselección sin eliminar al entrenador ni su equipo.
    @PostMapping("/entrenadores/salir")
    public String salir(HttpSession session) {
        session.removeAttribute("entrenador");
        return "redirect:/entrenadores";
    }

    @GetMapping("/entrenadores/{id}/editar")
    public String formularioEditar(@PathVariable int id, Model model) {

        Entrenador entrenador = servicio.findById(id);

        model.addAttribute("entrenador", entrenador);

        return "formulario-entrenador";
    }

    @GetMapping("/entrenadores/nuevo")
    public String formularioEntrenador(Model model) {

        model.addAttribute("entrenador", null);

        return "formulario-entrenador";
    }

    @PostMapping("/entrenadores/eliminar")
    public String eliminar(HttpSession session, @RequestParam("id") int id) {
        try {
            servicio.eliminar(id);
            Entrenador seleccionado = (Entrenador) session.getAttribute("entrenador");
            if (seleccionado != null && seleccionado.getId() == id) {
                session.removeAttribute("entrenador");
            }
        } catch (EntrenadorException e) {
            session.setAttribute("error", e.getMessage());
        }
        return "redirect:/entrenadores";
    }

    @GetMapping("/equipo")
    public String mostrarEquipo(HttpSession session, Model model) {

        Entrenador entrenador =
                (Entrenador) session.getAttribute("entrenador");

        if (entrenador != null) {
            List<Pokemon> pokemons = new ArrayList<>();

            for (Integer idPokemon : entrenador.getEquipo()) {
                pokemons.add(pokemonService.findById(idPokemon));
            }

            model.addAttribute("pokemons", pokemons);
        }

        return "equipo";
    }


    @PostMapping("/pokemon/equipo/eliminar")
    public String eliminarPokemon(HttpSession session,
                                  @RequestParam("id") int pokemonId) {
        try {
            Entrenador entrenador = (Entrenador) session.getAttribute("entrenador");

            if (entrenador != null) {
                Entrenador actualizado = servicio.quitarPokemon(entrenador.getId(), pokemonId);
                session.setAttribute("entrenador", actualizado);
            }

        } catch (EntrenadorException e) {
            session.setAttribute("error", e.getMessage());
        }

        return "redirect:/equipo";
    }

    @PostMapping("/pokemon/equipo/anadir")
    public String anadirPokemon(HttpSession session,
                                @RequestParam("id") int pokemonId) {
        try {
            Entrenador entrenador = (Entrenador) session.getAttribute("entrenador");

            if (entrenador != null) {
                Entrenador actualizado = servicio.anadirPokemon(
                        entrenador.getId(), pokemonId);

                session.setAttribute("entrenador", actualizado);
            }

        } catch (EntrenadorException e) {
            session.setAttribute("error", e.getMessage());
        }

        return "redirect:/equipo";
    }






    // TODO ALUMNADO: GET /entrenadores/nuevo -> mostrar formulario de creación.
    // TODO ALUMNADO: POST /entrenadores/crear -> llamar al servicio, capturar EntrenadorException.
    // TODO ALUMNADO: GET /entrenadores/{id}/editar -> precargar el formulario.
    // TODO ALUMNADO: POST /entrenadores/{id}/editar -> guardar, mostrar errores y refrescar sesión si corresponde.
    // TODO ALUMNADO: POST /entrenadores/{id}/eliminar -> borrar y quitar de sesión si estaba seleccionado.
}
