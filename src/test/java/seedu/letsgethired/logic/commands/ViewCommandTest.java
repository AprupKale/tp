package seedu.letsgethired.logic.commands;

import seedu.letsgethired.commons.core.index.Index;
import seedu.letsgethired.logic.Messages;
import seedu.letsgethired.model.Model;
import seedu.letsgethired.model.ModelManager;
import seedu.letsgethired.model.UserPrefs;
import seedu.letsgethired.model.application.InternApplication;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.letsgethired.logic.commands.CommandTestUtil.*;
import static seedu.letsgethired.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.letsgethired.testutil.TypicalIndexes.INDEX_FIRST_APPLICATION;
import static seedu.letsgethired.testutil.TypicalIndexes.INDEX_SECOND_APPLICATION;
import static seedu.letsgethired.testutil.TypicalInternApplications.getTypicalInternTracker;

public class ViewCommandTest {
    private Model model = new ModelManager(getTypicalInternTracker(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        InternApplication internApplicationToView = model
                .getFilteredInternApplicationList()
                .get(INDEX_FIRST_APPLICATION
                        .getZeroBased());
        ViewCommand viewCommand = new ViewCommand(INDEX_FIRST_APPLICATION);

        String expectedMessage = String.format(ViewCommand.MESSAGE_VIEW_INTERN_APPLICATION_SUCCESS,
                Messages.format(internApplicationToView));

        ModelManager expectedModel = new ModelManager(model.getInternTracker(), new UserPrefs());
        expectedModel.setCurrentInternApplication(internApplicationToView);
        assertCommandSuccess(viewCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredInternApplicationList().size() + 1);
        ViewCommand viewCommand = new ViewCommand(outOfBoundIndex);

        assertCommandFailure(viewCommand, model, Messages.MESSAGE_INVALID_INTERN_APPLICATION_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showInternApplicationAtIndex(model, INDEX_FIRST_APPLICATION);

        InternApplication internApplicationToView = model
                .getFilteredInternApplicationList().get(INDEX_FIRST_APPLICATION
                        .getZeroBased());
        ViewCommand viewCommand = new ViewCommand(INDEX_FIRST_APPLICATION);

        String expectedMessage = String.format(ViewCommand.MESSAGE_VIEW_INTERN_APPLICATION_SUCCESS,
                Messages.format(internApplicationToView));

        Model expectedModel = new ModelManager(model.getInternTracker(), new UserPrefs());
        showInternApplicationAtIndex(expectedModel, INDEX_FIRST_APPLICATION);
        expectedModel.setCurrentInternApplication(internApplicationToView);

        assertCommandSuccess(viewCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showInternApplicationAtIndex(model, INDEX_FIRST_APPLICATION);

        Index outOfBoundIndex = INDEX_SECOND_APPLICATION;
        // ensures that outOfBoundIndex is still in bounds of intern tracker list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getInternTracker().getApplicationList().size());

        ViewCommand viewCommand = new ViewCommand(outOfBoundIndex);
        showInternApplicationAtIndex(model, INDEX_FIRST_APPLICATION);
        assertCommandFailure(viewCommand, model, Messages.MESSAGE_INVALID_INTERN_APPLICATION_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ViewCommand viewFirstCommand = new ViewCommand(INDEX_FIRST_APPLICATION);
        ViewCommand viewSecondCommand = new ViewCommand(INDEX_SECOND_APPLICATION);

        // same object -> returns true
        assertTrue(viewFirstCommand.equals(viewFirstCommand));

        // same values -> returns true
        ViewCommand viewFirstCommandCopy = new ViewCommand(INDEX_FIRST_APPLICATION);
        assertTrue(viewFirstCommand.equals(viewFirstCommandCopy));

        // different types -> returns false
        assertFalse(viewFirstCommand.equals(1));

        // null -> returns false
        assertFalse(viewFirstCommand.equals(null));

        // different intern application -> returns false
        assertFalse(viewFirstCommand.equals(viewSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        ViewCommand viewCommand = new ViewCommand(targetIndex);
        String expected = ViewCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, viewCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoInternApplication(Model model) {
        model.updateFilteredInternApplicationList(p -> false);

        assertTrue(model.getFilteredInternApplicationList().isEmpty());
    }
}
