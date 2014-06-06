/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.ui.documents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.carewebframework.cal.api.query.AbstractServiceContext.DateMode;
import org.carewebframework.cal.api.query.IDataFilter;
import org.carewebframework.cal.api.query.IQueryResult;
import org.carewebframework.cal.ui.reporting.controller.AbstractListController;
import org.carewebframework.cal.ui.reporting.model.ServiceContext;
import org.carewebframework.ui.zk.ZKUtil;
import org.carewebframework.vista.api.documents.Document;
import org.carewebframework.vista.api.documents.DocumentCategory;
import org.carewebframework.vista.api.documents.DocumentListDataService;
import org.carewebframework.vista.api.documents.DocumentService;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ext.Selectable;

/**
 * Controller for the list-based display of clinical documents.
 */
public class DocumentListController extends AbstractListController<Document> {

    /**
     * Handles filtering by document category.
     */
    private class DataFilter implements IDataFilter<Document> {

        @Override
        public boolean include(Document document) {
            DocumentCategory filter = getCurrentFilter();
            return filter == null || document.hasCategory(filter);
        }

        @Override
        public boolean requiresFetch() {
            return false;
        }

    }

    private static final long serialVersionUID = 1L;

    private Button btnClear;

    private Button btnView;

    private String viewText; //default view selected documents

    private final String lblBtnViewSelectAll = Labels.getLabel("vistadocuments.plugin.btn.view.selectall.label");

    private Combobox cboFilter;

    private Label lblFilter;

    private Label lblInfo;

    private DocumentCategory fixedFilter;

    private final List<DocumentCategory> allCategories;

    public DocumentListController(final DocumentService service) {
        super(new DocumentListDataService(service), "vistadocuments", "TIU", "documentsPrint.css");
        setPaging(false);
        registerDataFilter(new DataFilter());
        allCategories = service.getCategories();
    }

    @Override
    public void initializeController() {
        super.initializeController();
        this.viewText = this.btnView.getLabel();
        getContainer().registerProperties(this, "fixedFilter");
    }

    @Override
    protected ServiceContext<Document> getServiceContext() {
        ServiceContext<Document> ctx = super.getServiceContext();
        ctx.setParam("category", getCurrentFilter());
        return ctx;
    }
    
    /**
     * This is a good place to update the filter list.
     */
    @Override
    protected void processResult(IQueryResult<Document> queryResult) {
        super.processResult(queryResult);
        updateListFilter(queryResult.getResults());
    }

    /**
     * Limit categories in category filter to only those present in the unfiltered document list.
     *
     * @param documents The unfiltered document list.
     */
    private void updateListFilter(List<Document> documents) {
        if (this.fixedFilter != null) {
            return;
        }

        final DocumentCategory currentFilter = getCurrentFilter();
        final List<Comboitem> items = this.cboFilter.getItems();
        final List<DocumentCategory> categories = new ArrayList<DocumentCategory>();

        while (items.size() > 1) {
            items.remove(1);
        }

        if (currentFilter != null) {
            categories.add(currentFilter);
        }

        if (documents != null) {
            for (final Document doc : documents) {
                DocumentCategory cat = doc.getCategory();
                
                if (cat != null) {
                    if (!categories.contains(cat)) {
                        categories.add(cat);
                    }
                }
            }
        }

        Collections.sort(categories);
        this.cboFilter.setSelectedIndex(0);

        for (final DocumentCategory cat : categories) {
            final Comboitem item = this.cboFilter.appendItem(cat.getName());
            item.setValue(cat);

            if (cat.equals(currentFilter)) {
                this.cboFilter.setSelectedItem(item);
            }
        }
    }

    /**
     * Returns the currently active category filter.
     *
     * @return
     */
    private DocumentCategory getCurrentFilter() {
        return this.fixedFilter != null ? this.fixedFilter
                : this.cboFilter.getSelectedIndex() > 0 ? (DocumentCategory) this.cboFilter.getSelectedItem().getValue()
                        : null;
    }

    /**
     * Handle change in category filter selection.
     */
    public void onSelect$cboFilter() {
        filterChanged();
    }

    /**
     * Update the display count of selected documents.
     *
     * @param selCount
     */
    private void updateSelectCount(int selCount) {
        if (selCount == 0) {
            this.btnView.setLabel(this.lblBtnViewSelectAll);
            this.btnClear.setDisabled(true);
        } else {
            this.btnView.setLabel(this.viewText + " (" + selCount + ")");
            this.btnClear.setDisabled(false);
        }
    }

    /**
     * Update selection count.
     */
    public void onSelect$listBox() {
        updateSelectCount(listBox.getSelectedCount());
    }

    /**
     * Double-clicking enters document view mode.
     *
     * @param event
     * @throws Exception
     */
    public void onDoubleClick$listBox(Event event) throws Exception {
        Component cmpt = ZKUtil.getEventOrigin(event).getTarget();

        if (cmpt instanceof Listitem) {
            Listitem item = (Listitem) cmpt;

            if (item != null) {
                item.setSelected(true);
            }

            Events.postEvent(Events.ON_CLICK, btnView, null);
        }
    }

    /**
     * Clear selected items
     */
    @Override
    public void clearSelection() {
        super.clearSelection();
        updateSelectCount(0);
    }

    /**
     * Triggers document view mode.
     */
    public void onClick$btnView() {
        Events.postEvent("onViewOpen", root, true);
    }

    /**
     * Returns a list of currently selected documents, or if no documents are selected, of all
     * documents.
     *
     * @return
     */
    protected List<Document> getSelectedDocuments() {
        return getObjects(listBox.getSelectedCount() > 0);
    }

    /**
     * Returns the fixed filter, if any.
     *
     * @return
     */
    public String getFixedFilter() {
        return fixedFilter == null ? null : fixedFilter.getName();
    }

    /**
     * Sets the fixed filter.
     *
     * @param name
     */
    public void setFixedFilter(String name) {
        fixedFilter = findCategory(name);
        this.cboFilter.setVisible(fixedFilter == null);
        this.lblFilter.setVisible(fixedFilter != null);
        this.lblFilter.setValue(fixedFilter == null ? null : fixedFilter.getName());
        refresh();
    }

    private DocumentCategory findCategory(String name) {
        if (name != null && !name.isEmpty()) {
            for (DocumentCategory cat : allCategories) {
                if (cat.getName().equals(name)) {
                    return cat;
                }
            }
        }
        
        return null;
    }

    @Override
    protected Date getDate(Document result, DateMode dateMode) {
        return result.getDateTime();
    }

    @Override
    protected void setListModel(ListModel<Document> model) {
        super.setListModel(model);
        int docCount = model == null ? 0 : model.getSize();
        lblInfo.setValue(docCount + " document(s)");
        btnView.setDisabled(docCount == 0);
        updateSelectCount(((Selectable<?>) model).getSelection().size());
    }
}