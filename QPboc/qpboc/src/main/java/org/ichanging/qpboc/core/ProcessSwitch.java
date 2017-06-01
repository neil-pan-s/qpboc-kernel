package org.ichanging.qpboc.core;

import org.ichanging.qpboc.platform.LogUtil;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by ChangingP on 16/6/23.
 */
public abstract class ProcessSwitch{

    private static final String TAG = "ProcessSwitch";

    private ProcessAble     _curProcess = null;
    private ProcessAble     _nextProcess = null;

    private ArrayList<ProcessAble> _curProcessTable = null;
    private Iterator<ProcessAble>  _iteratorProcessTable = null;

    public final void setProcessTable( ArrayList<ProcessAble> processTable )
    {
        _curProcessTable = processTable;
        _iteratorProcessTable = processTable.iterator();
    }

    public final ArrayList<ProcessAble> getProcessTable()
    {
        return _curProcessTable;
    }

    public final ProcessAble getCurrentProcess()
    {
        return _nextProcess;
    }

    public final ProcessAble getNextProcess()
    {
        return _nextProcess;
    }

    public final void setNextProcess(ProcessAble process)
    {
        _curProcess = _nextProcess;
        _nextProcess =  process;
    }

    /**
     *  move to process index matched
     *
     * @param index start whit index 0
     */
    public void moveToProcess(int index)
    {
        int i = -1;

        if(index >= _curProcessTable.size())
        {
            return;
        }

        _iteratorProcessTable = _curProcessTable.iterator();

        while (_iteratorProcessTable.hasNext())
        {
            i++;
            _iteratorProcessTable.next();

            if(index == i)
            {
                break;
            }
        }
    }

    public final ProcessAble nextProcess()
    {
        if (_iteratorProcessTable.hasNext()) {

            setNextProcess(_iteratorProcessTable.next());

            LogUtil.w(TAG,"Next Process is - "  + getNextProcess());

            return getNextProcess();
        }

        return null;
    }

    /**
     *  implements for different process order
     *
     *
     * @param curProcessResult
     */
    public abstract void onNextProcess(int curProcessResult);

}
