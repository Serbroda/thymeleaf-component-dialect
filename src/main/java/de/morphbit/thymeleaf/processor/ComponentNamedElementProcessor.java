package de.morphbit.thymeleaf.processor;

import de.morphbit.thymeleaf.helper.FragmentHelper;
import de.morphbit.thymeleaf.helper.WithHelper;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IElementTag;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.processor.element.AbstractElementModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.Collections.singleton;

public class ComponentNamedElementProcessor extends AbstractElementModelProcessor {

    private static final String THYMELEAF_FRAGMENT_PREFIX = "th";
    private static final String THYMELEAF_FRAGMENT_ATTRIBUTE = "fragment";
    private static final String REPLACE_CONTENT_TAG = "tc:content";
    private static final String DYNAMIC_ATT_PREFIX = "th:";

    private static final int PRECEDENCE = 350;

    private final Set<String> excludeAttributes = singleton("params");
    private final String fragmentName;

    public ComponentNamedElementProcessor(final String dialectPrefix, final String tagName, final String fragmentName) {
        super(TemplateMode.HTML, dialectPrefix, tagName, true, null, false, PRECEDENCE);
        this.fragmentName = fragmentName;
    }

    @Override
    protected void doProcess(ITemplateContext context, IModel model, IElementModelStructureHandler structureHandler) {
        IProcessableElementTag tag = processElementTag(context, model);
        Map<String, String> attrMap = processAttribute(context, tag);

        String param = attrMap.get("params");

        IModel base = model.cloneModel();
        base.remove(0);

        if (base.size() > 1) {
            base.remove(base.size() - 1);
        }

        IModel frag = FragmentHelper.getFragmentModel(context, fragmentName + (param == null ? "" : "(" + param + ")"),
                structureHandler, THYMELEAF_FRAGMENT_PREFIX, THYMELEAF_FRAGMENT_ATTRIBUTE);

        model.reset();
        model.addModel(mergeModel(frag, base, REPLACE_CONTENT_TAG));

        processVariables(attrMap, context, structureHandler, excludeAttributes);
    }

    private IProcessableElementTag processElementTag(ITemplateContext context, IModel model) {
        ITemplateEvent firstEvent = model.get(0);
        for (IProcessableElementTag tag : context.getElementStack()) {
            if (locationMatches(firstEvent, tag)) {
                return tag;
            }
        }
        return null;
    }

    private boolean locationMatches(ITemplateEvent a, ITemplateEvent b) {
        return Objects.equals(a.getTemplateName(), b.getTemplateName())
                && Objects.equals(a.getLine(), b.getLine())
                && Objects.equals(a.getCol(), b.getCol());
    }

    private void processVariables(Map<String, String> attrMap, ITemplateContext context,
                                    IElementModelStructureHandler structureHandler, Set<String> excludeAttr) {
        for (Map.Entry<String, String> entry : attrMap.entrySet()) {
            if (excludeAttr.contains(entry.getKey()) || entry.getKey().startsWith(this.getDialectPrefix() + ":")) {
                continue;
            }
            String val = entry.getValue();
            if(val == null) {
            	val = "${true}";
            }
            WithHelper.processWith(context, entry.getKey() + "=" + val, structureHandler);
        }
    }

    private Map<String, String> processAttribute(final ITemplateContext context, IProcessableElementTag tag) {
        Map<String, String> attMap = new HashMap<>();
        for (final IAttribute attribute : tag.getAllAttributes()) {
            String completeName = attribute.getAttributeCompleteName();
            if (!completeName.startsWith(DYNAMIC_ATT_PREFIX)) {
                attMap.put(completeName, attribute.getValue());
            }
        }

        return attMap;
    }

    private IModel mergeModel(IModel fragment, IModel body, final String replaceTag) {
        IModel mergedModel = insert(fragment, body, replaceTag);
        mergedModel = remove(mergedModel, replaceTag);
        mergedModel = remove(mergedModel, replaceTag);
        return mergedModel;
    }

    private IModel insert(IModel fragment, IModel body, final String replaceTag) {
        IModel mergedModel = fragment.cloneModel();
        int size = mergedModel.size();
        ITemplateEvent event = null;
        for (int i = 0; i < size; i++) {
            event = mergedModel.get(i);
            if (event instanceof IElementTag) {
                if (event.toString().contains(replaceTag)) {
                    mergedModel.insertModel(i, body);
                    break;
                }
            }
        }
        return mergedModel;
    }

    private IModel remove(IModel fragment, final String replaceTag) {
        IModel mergedModel = fragment.cloneModel();
        int size = mergedModel.size();
        ITemplateEvent event = null;
        for (int i = 0; i < size; i++) {
            event = mergedModel.get(i);
            if (event instanceof IElementTag) {
                if (event.toString().contains(replaceTag)) {
                    mergedModel.remove(i);
                    break;
                }
            }
        }
        return mergedModel;
    }
}
