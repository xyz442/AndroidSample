package cz.androidsample.ui.hierarchy

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cz.androidsample.R
import cz.androidsample.ui.hierarchy.adapter.SimpleHierarchyAdapter
import cz.androidsample.ui.hierarchy.model.HierarchyNode
import kotlinx.android.synthetic.main.fragment_hierarchy.*

/**
 * Created by cz on 2017/10/13.
 */
class HierarchyFragment: Fragment(){
    private lateinit var node: HierarchyNode
    companion object {
        fun newInstance(node: HierarchyNode):Fragment{
            val fragment=HierarchyFragment()
            fragment.node=node
            return fragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hierarchy,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        hierarchyView.setAdapter(SimpleHierarchyAdapter(context,node))
    }
}