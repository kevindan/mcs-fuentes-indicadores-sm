package pe.gob.minsa.indicadores.infraestructure.controller;

import pe.gob.minsa.indicadores.domain.ports.in.TableUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/operaciones")
@Tag(name = "Gestión de Tablas")
@RequiredArgsConstructor
public class TableController {

    @Autowired
    private TableUseCase tableUseCase;

    @GetMapping
    public String showPage(Model model) {
        List<String> tables = tableUseCase.listAllTables();
        model.addAttribute("tables", tables);
        return "indicadores";
    }

    @PostMapping("/carga/tabla")
    public String uploadCsv(@RequestParam("file") MultipartFile file,
                          RedirectAttributes redirectAttributes) {
        try {
            String filename = file.getOriginalFilename();
            tableUseCase.createTableFromCsv(file);
            redirectAttributes.addFlashAttribute("uploadMessage", 
                "✅ Archivo \"" + filename + "\" cargado correctamente.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("uploadMessage", 
                "❌ Error al procesar el archivo: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("uploadMessage", 
                "❌ " + e.getMessage());
        }
        return "redirect:/operaciones";
    }

    @PostMapping("/mantenimiento/tabla/{nombreTabla}")
    public String deleteTable(@PathVariable String nombreTabla,
                            RedirectAttributes redirectAttributes) {
        try {
            tableUseCase.deleteTable(nombreTabla);
            redirectAttributes.addFlashAttribute("deleteMessage", 
                "🗑️ Tabla \"" + nombreTabla + "\" eliminada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("deleteMessage", 
                "❌ No se pudo eliminar la tabla \"" + nombreTabla + "\": " + e.getMessage());
        }
        return "redirect:/operaciones";
    }
}