package es.mariana.dweb.controller;

import es.mariana.dweb.model.entities.Pokemon;
import es.mariana.dweb.model.service.PokemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/informacion")
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

    @GetMapping("/tabla")
    public String pokemonTabla(Model model) {
        ArrayList<Pokemon> listaPokemon = pokemonService.findRandomList(10);
        model.addAttribute("listaPokemons",listaPokemon);
        model.addAttribute("fecha", LocalDateTime.now());
        return "tabla";
    }

    @GetMapping("/galeria")
    public String pokemonGaleria(Model model) {
        ArrayList<Pokemon> galeriaPokemon = pokemonService.findRandomList(12);
        model.addAttribute("galeriaPokemon",galeriaPokemon);
        model.addAttribute("fecha", LocalDateTime.now());
        return "galeria";
    }

}
