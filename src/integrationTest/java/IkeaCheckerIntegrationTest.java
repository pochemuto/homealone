import java.io.IOException;
import java.util.List;

import com.pochemuto.homealone.HomealoneApplication;
import com.pochemuto.homealone.Profiles;
import com.pochemuto.homealone.ikea.IkeaChecker;
import com.pochemuto.homealone.ikea.Item;
import com.pochemuto.homealone.ikea.ItemRepository;
import com.pochemuto.homealone.ikea.NewItemsMailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static com.pochemuto.homealone.util.CustomArgumentMatchers.notEmpty;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = HomealoneApplication.class)
@ActiveProfiles(Profiles.INTEGRATION_TEST)
class IkeaCheckerIntegrationTest {

    @Autowired
    private IkeaChecker ikeaChecker;

    @Autowired
    private ItemRepository itemRepository;

    @MockBean
    private NewItemsMailSender mailSender;

    @BeforeEach
    public void setUp() {
        itemRepository.deleteAll();
    }

    @Test
    void checkIkea() throws IOException {
        assertThat(itemRepository.count()).isZero();

        ikeaChecker.check();
        int total = ikeaChecker.getTotal();
        var items = itemRepository.findAll();

        assertThat(total).isGreaterThan(0);
        assertThat(items).hasSize(total);

        assertThat(items)
                .extracting(Item::getPrice)
                .doesNotContainNull()
                .allMatch(price -> price.intValue() >= 2_900 && price.intValue() < 80_000);

        assertThat(items)
                .extracting(Item::isReduced)
                .contains(true, false);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Item>> addedCaptor = ArgumentCaptor.forClass(List.class);

        verify(mailSender).onItemsChanged(addedCaptor.capture(), eq(emptyList()), eq(emptyList()));
        assertThat(addedCaptor.getValue()).containsExactlyInAnyOrderElementsOf(items);
    }


    @Test
    void dontSaveIfListenerFailed() {
        var exception = new RuntimeException("error happened");

        doThrow(exception).when(mailSender).onItemsChanged(any(), any(), any());

        assertThatThrownBy(() -> ikeaChecker.check()).isSameAs(exception);

        verify(mailSender).onItemsChanged(argThat(notEmpty()), any(), any());

        assertThat(itemRepository.count()).isZero();
    }

}
