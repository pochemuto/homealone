import java.io.IOException;

import com.pochemuto.homealone.HomealoneApplication;
import com.pochemuto.homealone.Profile;
import com.pochemuto.homealone.ikea.IkeaChecker;
import com.pochemuto.homealone.ikea.Item;
import com.pochemuto.homealone.ikea.ItemRepository;
import com.pochemuto.homealone.ikea.NewItemsMailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static com.pochemuto.homealone.util.CustomArgumentMatchers.notEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = HomealoneApplication.class)
@ActiveProfiles(Profile.INTEGRATION_TEST)
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

        var items = itemRepository.findAll();
        assertThat(items)
                .extracting(Item::getName)
                .containsExactlyInAnyOrder(
                        "HJÄLPSAM ЭЛЬПСАМ",
                        "LAGAN ЛАГАН",
                        "SKINANDE СКИНАНДЕ",
                        "HYGIENISK ХИГИЕНИСК"
                );

        assertThat(items)
                .extracting(Item::getPrice)
                .doesNotContainNull()
                .allMatch(price -> price.intValue() > 15_000 && price.intValue() < 60_000);

        assertThat(items)
                .extracting(Item::isReduced)
                .contains(true, false);

        verify(mailSender).onNewItems(items);
    }

    @Test
    void rollbackTransactionOnListenerFail() {
        var exception = new RuntimeException("error happened");

        doThrow(exception).when(mailSender).onNewItems(any());

        assertThatThrownBy(() -> ikeaChecker.check()).isSameAs(exception);

        verify(mailSender).onNewItems(argThat(notEmpty()));

        assertThat(itemRepository.count()).isZero();
    }

}