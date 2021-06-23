package org.js.azdanov.springfresh.controllers;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.js.azdanov.springfresh.controllers.requests.CreateListingForm;
import org.js.azdanov.springfresh.dtos.ListingDTO;
import org.js.azdanov.springfresh.exceptions.ForbiddenException;
import org.js.azdanov.springfresh.services.AreaService;
import org.js.azdanov.springfresh.services.CategoryService;
import org.js.azdanov.springfresh.services.ListingService;
import org.js.azdanov.springfresh.services.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequiredArgsConstructor
public class CreateListingController {
  public final UserService userService;
  public final AreaService areaService;
  public final ListingService listingService;
  private final CategoryService categoryService;

  @GetMapping("/listings/create")
  public String create(Model model) {
    var areas = areaService.getAllAreasTree();
    var categories = categoryService.getAllCategories();

    model.addAttribute("areas", areas);
    model.addAttribute("categories", categories);

    if (!model.containsAttribute("listing")) {
      model.addAttribute("listing", new CreateListingForm());
    }

    return "listings/create";
  }

  @PostMapping("/listings")
  public String store(
      @Valid @ModelAttribute("listing") CreateListingForm listingForm,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      @AuthenticationPrincipal UserDetails userDetails) {

    if (bindingResult.hasErrors()) {
      redirectAttributes.addFlashAttribute("listing", listingForm);
      redirectAttributes.addFlashAttribute(
          "org.springframework.validation.BindingResult.listing", bindingResult);
      return "redirect:listings/create";
    }

    listingForm.setUserEmail(userDetails.getUsername());
    var listing = listingService.createListing(listingForm);

    return "redirect:listings/%d/edit".formatted(listing.id());
  }

  @GetMapping("/listings/{listingId}/edit")
  public String edit(
      @PathVariable Integer listingId,
      Model model,
      @AuthenticationPrincipal UserDetails userDetails,
      UriComponentsBuilder uriComponentsBuilder) {
    var areas = areaService.getAllAreasTree();
    var categories = categoryService.getAllCategories();
    var listing = listingService.getById(listingId);

    if (!listing.user().email().equals(userDetails.getUsername())) {
      throw new ForbiddenException(
          "edit Listing(%d, %s) by user(%s)"
              .formatted(listing.id(), listing.user().email(), userDetails.getUsername()));
    }

    model.addAttribute("areas", areas);
    model.addAttribute("categories", categories);
    if (!model.containsAttribute("listing")) {
      model.addAttribute("listing", new CreateListingForm(listing));
    }
    model.addAttribute("listingURI", getListingURI(uriComponentsBuilder, listing));

    return "listings/edit";
  }

  @PutMapping("/listings")
  public String update(
      @Valid @ModelAttribute("listing") CreateListingForm listingForm,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes,
      @AuthenticationPrincipal UserDetails userDetails) {

    if (bindingResult.hasErrors()) {
      redirectAttributes.addFlashAttribute("listing", listingForm);
      redirectAttributes.addFlashAttribute(
          "org.springframework.validation.BindingResult.listing", bindingResult);
      return "redirect:listings/%d/edit".formatted(listingForm.getId());
    }

    if (!listingForm.getUserEmail().equals(userDetails.getUsername())
        && !listingService.belongsTo(listingForm.getId(), listingForm.getUserEmail())) {
      throw new ForbiddenException(
          "update Listing(%d, %s) by user(%s)"
              .formatted(listingForm.getId(), listingForm.getUserEmail(), userDetails.getUsername()));
    }

    ListingDTO listing = listingService.updateListing(listingForm);

    // TODO: Check if pay button was clicked

    return "redirect:listings/%d/edit".formatted(listing.id());
  }

  private String getListingURI(UriComponentsBuilder uriComponentsBuilder, ListingDTO listing) {
    return uriComponentsBuilder
        .uriComponents(
            MvcUriComponentsBuilder.fromMethodName(
                    ListingController.class,
                    "show",
                    listing.area().slug(),
                    listing.category().slug(),
                    listing.id(),
                    null,
                    null)
                .build())
        .toUriString();
  }
}
