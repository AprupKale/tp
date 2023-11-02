package seedu.letsgethired.logic.parser;

import static seedu.letsgethired.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.letsgethired.logic.commands.CommandTestUtil.COMPANY_SORT_ORDER_ASCENDING;
import static seedu.letsgethired.logic.commands.CommandTestUtil.CYCLE_SORT_ORDER_DESCENDING;
import static seedu.letsgethired.logic.commands.CommandTestUtil.DEADLINE_SORT_ORDER_DESCENDING;
import static seedu.letsgethired.logic.commands.CommandTestUtil.INVALID_COMPANY_SORT_ORDER;
import static seedu.letsgethired.logic.commands.CommandTestUtil.INVALID_CYCLE_SORT_ORDER;
import static seedu.letsgethired.logic.commands.CommandTestUtil.INVALID_ROLE_SORT_ORDER;
import static seedu.letsgethired.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.letsgethired.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.letsgethired.model.application.InternApplicationComparator.COMPANY_COMPARATOR_ASCENDING;
import static seedu.letsgethired.model.application.InternApplicationComparator.CYCLE_COMPARATOR_DESCENDING;
import static seedu.letsgethired.model.application.InternApplicationComparator.DEADLINE_COMPARATOR_DESCENDING;

import org.junit.jupiter.api.Test;

import seedu.letsgethired.logic.commands.SortCommand;

public class SortCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
    private static final String MESSAGE_INVALID_SORT_ORDER = String.format(MESSAGE_INVALID_FORMAT,
            SortOrder.MESSAGE_CONSTRAINTS);

    private SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // No field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        // Invalid field specified
        assertParseFailure(parser, INVALID_COMPANY_SORT_ORDER, SortOrder.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, INVALID_ROLE_SORT_ORDER, SortOrder.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, INVALID_CYCLE_SORT_ORDER, SortOrder.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_oneValidField_success() {
        // One valid field specified
        // Check for referential equality in comparators
        SortCommand expectedCommand = new SortCommand(COMPANY_COMPARATOR_ASCENDING);
        assertParseSuccess(parser, COMPANY_SORT_ORDER_ASCENDING, expectedCommand);

        expectedCommand = new SortCommand(CYCLE_COMPARATOR_DESCENDING);
        assertParseSuccess(parser, CYCLE_SORT_ORDER_DESCENDING, expectedCommand);

        expectedCommand = new SortCommand(DEADLINE_COMPARATOR_DESCENDING);
        assertParseSuccess(parser, DEADLINE_SORT_ORDER_DESCENDING, expectedCommand);
    }

    // TODO: Add test cases for parsing multiple fields at the same time
}
