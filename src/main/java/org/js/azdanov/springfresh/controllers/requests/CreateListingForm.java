package org.js.azdanov.springfresh.controllers.requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.js.azdanov.springfresh.dtos.ListingDTO;

@Data
@NoArgsConstructor
public class CreateListingForm {
  private Integer id;

  @NotBlank
  @Size(max = 255)
  private String title;

  @NotBlank
  @Size(max = 2000)
  private String body;

  private boolean live;
  private String userEmail;
  @Positive private int areaId;
  @Positive private int categoryId;

  public CreateListingForm(ListingDTO listing) {
    id = listing.id();
    title = listing.title();
    body = listing.body();
    live = listing.live();
    userEmail = listing.user().email();
    areaId = listing.area().id();
    categoryId = listing.category().id();
  }
}
