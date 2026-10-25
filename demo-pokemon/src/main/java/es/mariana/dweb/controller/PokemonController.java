package es.mariana.dweb.controller;

import es.mariana.dweb.model.entities.Pokemon;
import es.mariana.dweb.model.service.PokemonService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller
public class PokemonController {

    @Autowired
    private PokemonService pokemonService;

    @GetMapping("/")
    public String home(Model model) {
        ArrayList<Pokemon> pokemons = pokemonService.obtenerPokemons();
        model.addAttribute("pokemons", pokemons);

        return "home";
    }

    @GetMapping("/pokemon/informacion")
    public String informacion(Model model) {
        Pokemon pokemon = pokemonService.findRandom();
        model.addAttribute("numeroPokemon",pokemon.getId());
        model.addAttribute("nombrePokemon",pokemon.getName());
        model.addAttribute("alturaPokemon",pokemon.getHeight());
        model.addAttribute("pesoPokemon",pokemon.getWeight());
        model.addAttribute("experienciaBasePokemon",pokemon.getBaseExperience());
        model.addAttribute("srcPokemon",pokemon.getImageURL());

        model.addAttribute("fecha", LocalDateTime.now());

        return "informacion";
    }

    @GetMapping("/pokemon/tabla")
    public String pokemonTabla(@RequestParam(value = "tipo", required = false) String tipo,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "alturaMin", required = false) Double alturaMin,
            @RequestParam(value = "pesoMax", required = false) Double pesoMax,
            @RequestParam(value = "orden", required = false) String orden,
            @RequestParam(value = "direccion", required = false) String direccion,
            Model model, HttpSession session) {

        ArrayList<Pokemon> todosPokemons = pokemonService.findAll();
        ArrayList<Pokemon> resultado = filtrarYOrdenar(todosPokemons, tipo, nombre, alturaMin, pesoMax, orden, direccion);
        ArrayList<Pokemon.Type> listaTipos = pokemonService.findAllTypes();


        model.addAttribute("nombre", nombre);
        model.addAttribute("alturaMin", alturaMin);
        model.addAttribute("pesoMax", pesoMax);
        model.addAttribute("orden", orden);
        model.addAttribute("direccion", direccion);
        model.addAttribute("nombreEntrenador",session.getAttribute("nombreEntrenador"));
        model.addAttribute("errorEquipo",session.getAttribute("errorEquipo"));

        model.addAttribute("tipo", tipo);
        model.addAttribute("listaPokemons", resultado);
        model.addAttribute("listaTipos", listaTipos);
        model.addAttribute("fecha", LocalDateTime.now());

        return "tabla";
    }

    @GetMapping("/pokemon/galeria")
    public String pokemonGaleria(@RequestParam(value = "tipo", required = false) String tipo,
                                 @RequestParam(value = "nombre", required = false) String nombre,
                                 @RequestParam(value = "alturaMin", required = false) Double alturaMin,
                                 @RequestParam(value = "pesoMax", required = false) Double pesoMax,
                                 @RequestParam(value = "orden", required = false) String orden,
                                 @RequestParam(value = "direccion", required = false) String direccion,
                                 Model model, HttpSession session) {

        ArrayList<Pokemon> todosPokemons = pokemonService.findAll();
        ArrayList<Pokemon> resultado = filtrarYOrdenar(todosPokemons, tipo, nombre, alturaMin, pesoMax, orden, direccion);
        ArrayList<Pokemon.Type> listaTipos = pokemonService.findAllTypes();


        model.addAttribute("nombre", nombre);
        model.addAttribute("alturaMin", alturaMin);
        model.addAttribute("pesoMax", pesoMax);
        model.addAttribute("orden", orden);
        model.addAttribute("direccion", direccion);
        model.addAttribute("nombreEntrenador",session.getAttribute("nombreEntrenador"));
        model.addAttribute("errorEquipo",session.getAttribute("errorEquipo"));

        model.addAttribute("tipo", tipo);
        model.addAttribute("listaTipos", listaTipos);
        model.addAttribute("galeriaPokemon",resultado);
        model.addAttribute("fecha", LocalDateTime.now());
        return "galeria";
    }

    @GetMapping("/pokemon/batalla")
    public String pokemonBatalla(Model model, HttpSession session) {
        Pokemon pokemon1 = (Pokemon) session.getAttribute("pokem1");
        Pokemon pokemon2 = (Pokemon) session.getAttribute("pokem2");
        model.addAttribute("pokem1",pokemon1);
        model.addAttribute("pokem2",pokemon2);
        model.addAttribute("nombreEntrenador",session.getAttribute("nombreEntrenador"));
        return "batalla";
    }

    @GetMapping("/pokemon/equipo")
    public String equipo(Model model,HttpSession session) {

        model.addAttribute("nombreEntrenador",session.getAttribute("nombreEntrenador"));
        model.addAttribute("errorEquipo",session.getAttribute("errorEquipo"));
        model.addAttribute("error",session.getAttribute("error"));
        model.addAttribute("pokemonsEntrenador",session.getAttribute("pokemonsEntrenador"));
        return "equipo";
    }

    @PostMapping("/pokemon/entrenador")
    public String entrenador(HttpSession session, @RequestParam(value = "nombreEntrenador") String nombreEntrenador) {
        if (nombreEntrenador == null || nombreEntrenador.isBlank()){
            session.setAttribute("error", true);
            session.removeAttribute("nombreEntrenador");
        } else {

            if (session.getAttribute("pokemonsEntrenador") == null){
                ArrayList<Pokemon> pokemonsEntrenador = new ArrayList<>();
                session.setAttribute("pokemonsEntrenador",pokemonsEntrenador);
            }
            session.setAttribute("nombreEntrenador",nombreEntrenador.trim());
            session.removeAttribute("error");
        }
        return "redirect:/pokemon/equipo";
    }

    @PostMapping("/pokemon/entrenador/salir")
    public String salir(HttpSession session) {
        session.removeAttribute("nombreEntrenador");
        session.invalidate();
        return "redirect:/pokemon/equipo";
    }

    @PostMapping("/pokemon/equipo/eliminar")
    public String eliminar(HttpSession session, @RequestParam("id") int id) {

         ArrayList<Pokemon> pokemonsEntrenador = (ArrayList<Pokemon>) session.getAttribute("pokemonsEntrenador");
        for (int i = 0; i < pokemonsEntrenador.size(); i++) {
            if (pokemonsEntrenador.get(i).getName().equals(pokemonService.findById(id).getName())){
                pokemonsEntrenador.remove(i);
                break;
            }
        }
        session.setAttribute("pokemonsEntrenador",pokemonsEntrenador);
        return "redirect:/pokemon/equipo";
    }


    @PostMapping("/pokemon/equipo/combatir")
    public String combatir(HttpSession session, @RequestParam("id") int id) {
        Pokemon pokemon1 = pokemonService.findById(id);
        Pokemon pokemon2 = pokemonService.findRandom();
        session.setAttribute("pokem1",pokemon1);
        session.setAttribute("pokem2",pokemon2);
        session.setAttribute("nombreEntrenador",session.getAttribute("nombreEntrenador"));
        return "redirect:/pokemon/batalla";
    }

    @PostMapping("/pokemon/entrenador/añadirAlEquipo")
    public String añadirAlEquipo(HttpSession session, @RequestParam("id") int id) {
        ArrayList<Pokemon> pokemonsEntrenador = (ArrayList<Pokemon>) session.getAttribute("pokemonsEntrenador");
        if (pokemonsEntrenador == null) {
            pokemonsEntrenador = new ArrayList<>();
        } else {

        }
        boolean estaLista = false;
        for (Pokemon pokemon : pokemonsEntrenador) {
            if (pokemon.getName().equals(pokemonService.findById(id).getName())){
                estaLista = true;
            }
        }

        if (estaLista) {
            session.setAttribute("errorEquipo", true);
        } else if (pokemonsEntrenador.size() >= 6) {
            session.setAttribute("errorEquipo", false);
            session.setAttribute("equipoLleno", true);
        } else {
            pokemonsEntrenador.add(pokemonService.findById(id));
            session.setAttribute("errorEquipo", false);
            session.setAttribute("equipoLleno", false);
        }


        session.setAttribute("pokemonsEntrenador",pokemonsEntrenador);
        return "redirect:/pokemon/galeria";
    }



    @GetMapping("/pokemon/{id}")
    public String pokemonDetalle(@PathVariable int id, Model model) {
        Pokemon pokemon = pokemonService.findById(id);

        model.addAttribute("numeroPokemon", pokemon.getId());
        model.addAttribute("nombrePokemon", pokemon.getName());
        model.addAttribute("alturaPokemon", pokemon.getHeight());
        model.addAttribute("pesoPokemon", pokemon.getWeight());
        model.addAttribute("experienciaBasePokemon", pokemon.getBaseExperience());
        model.addAttribute("srcPokemon", pokemon.getImageURL());
        model.addAttribute("fecha", LocalDateTime.now());

        return "informacion";
    }


    private ArrayList<Pokemon> filtrarYOrdenar(ArrayList<Pokemon> pokemons,String tipo,String nombre,Double alturaMin,Double pesoMax,String orden,String direccion) {
        ArrayList<Pokemon> resultado = new ArrayList<>();

        for (Pokemon poke : pokemons) {
            boolean cumple = true;
            if (nombre != null && !nombre.isBlank() && !poke.getName().toLowerCase().contains(nombre.trim().toLowerCase())) {
                cumple = false;
            }

            if (tipo != null && !tipo.isBlank() && !poke.getTypes().toString().toLowerCase().contains(tipo.trim().toLowerCase())) {
                cumple = false;
            }

            if (alturaMin != null && poke.getHeight() / 10.0 < alturaMin) {
                cumple = false;
            }

            if (pesoMax != null && poke.getWeight() / 10.0 > pesoMax) {
                cumple = false;
            }

            if (cumple) {
                resultado.add(poke);
            }
        }

        if (orden != null && !orden.isBlank()) {
            resultado.sort((p1, p2) -> {
                int comparacion = 0;

                switch (orden) {
                    case "name":
                        comparacion = p1.getName()
                                .compareToIgnoreCase(p2.getName());
                        break;

                    case "height":
                        comparacion = Double.compare(
                                p1.getHeight(), p2.getHeight());
                        break;

                    case "weight":
                        comparacion = Double.compare(
                                p1.getWeight(), p2.getWeight());
                        break;
                }

                if ("desc".equalsIgnoreCase(direccion)) {
                    comparacion = -comparacion;
                }

                return comparacion;
            });
        }

        return resultado;
    }



}
