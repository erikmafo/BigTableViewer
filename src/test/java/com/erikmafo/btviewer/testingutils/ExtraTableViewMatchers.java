package com.erikmafo.btviewer.testingutils;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.hamcrest.*;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by erikmafo on 24.12.17.
 */
public class ExtraTableViewMatchers {


    public static Matcher<TableView> hasColumnWithText(String text) {

        return new TypeSafeDiagnosingMatcher<TableView>() {
            @Override
            protected boolean matchesSafely(TableView item, Description mismatchDescription) {

                List<String> columns = new ArrayList<>();

                for (Object obj : item.getColumns()) {
                    String columnText = ((TableColumn)obj).getText();
                    if (columnText.equals(text)) {
                        return true;
                    }
                    columns.add(columnText);
                }

                mismatchDescription.appendText("was a table view containing columns with text: ");
                mismatchDescription.appendValueList("[", ",", "]", columns);

                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("A table view containing a column with the text: ").appendText(text);
            }
        };

    }


}
