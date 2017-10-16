package cz.androidsample.ui.hierarchy

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import cz.androidsample.R
import cz.androidsample.ui.hierarchy.model.HierarchyNode
import cz.androidsample.ui.hierarchy.widget.HierarchyLayout4
import kotlinx.android.synthetic.main.fragment_hierarchy4.*
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by cz on 2017/10/13.
 */
class Hierarchy4Fragment : Fragment(){
    private lateinit var node: HierarchyNode
    companion object {
        fun newInstance(node: HierarchyNode):Fragment{
            val fragment= Hierarchy4Fragment()
            fragment.node=node
            return fragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hierarchy4,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        hierarchyView.setAdapter(SimpleHierarchyAdapter(context,node))
        buttonScale.onClick {
            hierarchyView.setHierarchyScale(2f)
        }
    }

    /**
     * Created by cz on 2017/10/13.
     */
    class SimpleHierarchyAdapter(val context: Context, node:HierarchyNode): HierarchyLayout4.HierarchyAdapter(node){
        private val layoutInflate=LayoutInflater.from(context)
        override fun getView(parent:ViewGroup): View {
            return layoutInflate.inflate(R.layout.hierarchy_item,parent,false)
        }

        override fun bindView(view: View, node: HierarchyNode) {
            view.find<TextView>(R.id.viewClassNameText).text=node.name
            view.find<TextView>(R.id.viewResourceName).text=node.entryName
            view.find<TextView>(R.id.viewRectText).text=node.rect.toString()
            view.find<TextView>(R.id.viewDescriptionText).text=node.description
            view.setOnClickListener {
                Toast.makeText(context,"点击:${node.name} 节点!", Toast.LENGTH_SHORT).show()
            }
        }

    }
}