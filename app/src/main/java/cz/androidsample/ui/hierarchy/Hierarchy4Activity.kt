package cz.androidsample.ui.hierarchy

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup

import cz.androidsample.R
import cz.androidsample.annotation.ToolBar
import cz.androidsample.ui.hierarchy.model.HierarchyNode
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_hierarchy4.*
import org.jetbrains.anko.sdk25.coroutines.onClick

@ToolBar
class Hierarchy4Activity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hierarchy4)
        setTitle(intent.getStringExtra("title"))

        button.onClick {
            //将当前界面所有控件节点信息扫描出来
            val decorView=window.decorView
            val root= HierarchyNode(0,decorView::class.java.simpleName)
            if(decorView is ViewGroup){
                (0..decorView.childCount-1).
                        map { decorView.getChildAt(it) }.
                        forEach { hierarchyViewer(it,root,1) }
            }
            supportFragmentManager.
                    beginTransaction().
                    addToBackStack(null).
                    add(R.id.fragmentContainer, Hierarchy4Fragment.newInstance(root)).commit()
        }
    }

    /**
     * 遍历所有控件层级节点
     */
    private fun hierarchyViewer(view: View, parent: HierarchyNode, level:Int){
        val node= HierarchyNode(level,view::class.java.simpleName)
        //记录id
        node.id=view.id
        //记录控件描述
        node.description=view.contentDescription
        if(view.id!= View.NO_ID){
            //记录id
            node.entryName=resources.getResourceEntryName(view.id)
        }
        //记录控件所占矩阵
        val rect= Rect()
        view.getGlobalVisibleRect(rect)
        node.rect.set(rect)

        //记录父节点
        node.parent=parent
        //记录子节点
        parent.children.add(node)
        if(view is ViewGroup){
            (0..view.childCount-1).
                    map { view.getChildAt(it) }.
                    forEach { hierarchyViewer(it,node,level+1) }
        }
    }

}
