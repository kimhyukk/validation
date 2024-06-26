package hello.itemservice.web.validation;

import hello.itemservice.domain.item.*;
import hello.itemservice.web.validation.dto.ItemSaveDto;
import hello.itemservice.web.validation.dto.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v4/items")
@RequiredArgsConstructor
public class ValidationItemControllerV4 {

    private final ItemRepository itemRepository;


    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v4/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v4/addForm";
    }


    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute("item") ItemSaveDto itemSaveDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {


        //글로벌 오류
        if (itemSaveDto.getPrice() != null && itemSaveDto.getQuantity() != null) {
            int resultPrice = itemSaveDto.getPrice() * itemSaveDto.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }


        //검증에 실패하면 다시 입력 폼으로

        if (bindingResult.hasErrors()) {
            log.info("error = {}", bindingResult);
//            model.addAttribute("errors", bindingResult); 생략 가능
            return "validation/v4/addForm";
        }


        //성공 로직
        Item item = new Item(itemSaveDto.getItemName(), itemSaveDto.getPrice(), itemSaveDto.getQuantity());
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v4/items/{itemId}";
    }



    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @Validated @ModelAttribute("item") ItemUpdateDto itemUpdateDto, BindingResult bindingResult) {

        //글로벌 오류
        if (itemUpdateDto.getPrice() != null && itemUpdateDto.getQuantity() != null) {
            int resultPrice = itemUpdateDto.getPrice() * itemUpdateDto.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }


        //검증에 실패하면 다시 입력 폼으로

        if (bindingResult.hasErrors()) {
            log.info("error = {}", bindingResult);
//            model.addAttribute("errors", bindingResult); 생략 가능
            return "validation/v4/editForm";
        }


        itemRepository.update(itemId, itemUpdateDto);
        return "redirect:/validation/v4/items/{itemId}";
    }



}

