package org.extremecomponents.table.limit;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

public class TableLimitTest extends MockObjectTestCase {
    private Mock limitFactory;
    private TableLimit tableLimit;

    protected void setUp() throws Exception {
        limitFactory = mock(LimitFactory.class);
    }

    /**
     * Testing that the page returned is not greater than the pages that are
     * able to be displayed. The problem arises if using the state feature and
     * rows are deleted.
     * 
     * Example: I had 9 Records to be displayed, I set rowsDisplayed = 2. I navigated to
     * the 5th (last) page. I clicked some other link of the application to
     * change the screen. I deleted the 9th Record from the Database. When I
     * came back to the screen with eXtremeComponents, this is what I get.
     * Number of rows to be displayed (the Drop Down) is still 2, but the grid
     * displays more than 2, actually all 8 of them. But the text says "8
     * results found, displaying 9 to 8".
     * 
     * Remember that in the GUI that the row count will be bumped up by one
     * so we need to test that the row end is always greater than the row start.
     */
    public void testSetRowAttributes() {
        limitFactory.expects(once())
                    .method("getCurrentRowsDisplayed")
                    .will(returnValue(2));
        
        limitFactory.expects(once())
                     .method("getFilterSet")
                     .will(returnValue(new FilterSet()));
        
        limitFactory.expects(once())
                    .method("getSort")
                    .will(returnValue(new Sort()));
        
        limitFactory.expects(once())
                    .method("isExported")
                    .will(returnValue(false));
        
        limitFactory.expects(once())
                    .method("getPage")
                    .will(returnValue(5));

        tableLimit = new TableLimit((LimitFactory) limitFactory.proxy());

        tableLimit.setRowAttributes(5, 2); //fail when pass in 8 vs 9 totals rows

        assertTrue("page", tableLimit.getPage() == 3);
        assertTrue("current rows displayed", tableLimit.getCurrentRowsDisplayed() == 2);

        int rowStart = tableLimit.getRowStart();
        int rowEnd = tableLimit.getRowEnd();
        assertTrue("row end greater than start", rowEnd >= rowStart);
    }
}
